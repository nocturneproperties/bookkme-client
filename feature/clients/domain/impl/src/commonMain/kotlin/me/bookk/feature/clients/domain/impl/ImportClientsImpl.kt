package me.bookk.feature.clients.domain.impl

import me.bookk.core.domain.entity.Error
import me.bookk.feature.clients.domain.api.ImportClients
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.api.entity.ClientImportResult
import me.bookk.feature.clients.domain.datasource.ClientErrorCodes
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.uuid.Uuid

internal class ImportClientsImpl(
    private val clientsDataSource: ClientsDataSource
) : ImportClients {

    override suspend fun invoke(businessId: Uuid, entries: List<ClientImportEntry>): ClientImportResult {
        val results = entries
            .map(ClientImportEntry::trimmed)
            .chunked(MAX_ENTRIES_PER_REQUEST)
            .mapIndexed { requestIndex, requestEntries ->
                importWithRetry(businessId, requestEntries)
                    .also { clientsDataSource.saveClientsInDb(it.created) }
                    .withRejectedOffset(requestIndex * MAX_ENTRIES_PER_REQUEST)
            }
        return ClientImportResult(
            created = results.flatMap { it.created },
            rejected = results.flatMap { it.rejected }
        )
    }

    private suspend fun importWithRetry(businessId: Uuid, entries: List<ClientImportEntry>): ClientImportResult {
        return runCatching { clientsDataSource.importClients(businessId, entries) }
            .recoverCatching { error ->
                if (!error.isTakenByConcurrentWrite()) throw error
                clientsDataSource.importClients(businessId, entries)
            }
            .getOrThrow()
    }

    private fun Throwable.isTakenByConcurrentWrite(): Boolean {
        return this is Error.BusinessError && errorCode == ClientErrorCodes.CLIENT_EXISTS
    }

    private fun ClientImportResult.withRejectedOffset(offset: Int): ClientImportResult {
        return copy(rejected = rejected.map { it.copy(index = it.index + offset) })
    }

    private companion object {
        const val MAX_ENTRIES_PER_REQUEST = 500
    }
}

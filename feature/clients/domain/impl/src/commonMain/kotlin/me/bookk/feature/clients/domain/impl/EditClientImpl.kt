package me.bookk.feature.clients.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.clients.domain.api.EditClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientErrorCodes
import me.bookk.feature.clients.domain.datasource.ClientsDataSource

internal class EditClientImpl(
    private val clientsDataSource: ClientsDataSource,
) : EditClient {
    override suspend fun invoke(client: Client): Client {
        return runCatching { clientsDataSource.updateClient(client.trimmed()) }
            .onBusinessError {
                if (it.errorCode == ClientErrorCodes.CLIENT_EXISTS) {
                    throw EditClient.Error.EmailTaken()
                }
            }
            .getOrThrow()
            .also { clientsDataSource.saveClientsInDb(listOf(it)) }
    }
}

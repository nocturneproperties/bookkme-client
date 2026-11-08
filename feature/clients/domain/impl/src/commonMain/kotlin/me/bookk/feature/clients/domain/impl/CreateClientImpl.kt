package me.bookk.feature.clients.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.clients.domain.api.CreateClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientErrorCodes
import me.bookk.feature.clients.domain.datasource.ClientsDataSource

internal class CreateClientImpl(
    private val clientsDataSource: ClientsDataSource,
) : CreateClient {
    override suspend fun invoke(client: Client): Client {
        return runCatching { clientsDataSource.createClient(client.trimmed()) }
            .onBusinessError {
                if (it.errorCode == ClientErrorCodes.CLIENT_EXISTS) {
                    throw CreateClient.Error.EmailTaken()
                }
            }
            .getOrThrow()
            .also { clientsDataSource.saveClientsInDb(listOf(it)) }
    }
}

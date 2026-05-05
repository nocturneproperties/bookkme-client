package me.bookk.feature.clients.domain.impl

import me.bookk.feature.clients.domain.api.DeleteClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientsDataSource

internal class DeleteClientImpl(
    private val clientsDataSource: ClientsDataSource
) : DeleteClient {
    override suspend fun invoke(client: Client) {
        clientsDataSource.deleteClient(client.businessId, client.id)
        clientsDataSource.deleteClientInDb(client.id)
    }
}
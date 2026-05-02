package me.bookk.feature.business.domain.impl.client

import me.bookk.feature.business.domain.api.client.DeleteClient
import me.bookk.feature.business.domain.api.entity.Client
import me.bookk.feature.business.domain.datasource.ClientsDataSource

internal class DeleteClientImpl(
    private val clientsDataSource: ClientsDataSource
) : DeleteClient {
    override suspend fun invoke(client: Client) {
        clientsDataSource.deleteClient(client.businessId, client.id)
        clientsDataSource.deleteClientInDb(client.id)
    }
}
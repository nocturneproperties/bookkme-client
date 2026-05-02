package me.bookk.feature.business.domain.impl.client

import me.bookk.feature.business.domain.api.client.CreateClient
import me.bookk.feature.business.domain.api.entity.Client
import me.bookk.feature.business.domain.datasource.ClientsDataSource

internal class CreateClientImpl(
    private val clientsDataSource: ClientsDataSource
) : CreateClient {
    override suspend fun invoke(client: Client): Client {
        return clientsDataSource.createClient(client)
    }
}
package me.bookk.feature.clients.domain.impl

import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.uuid.Uuid

internal class GetClientImpl(
    private val clientsDataSource: ClientsDataSource
) : GetClient {
    override suspend fun invoke(id: Uuid): Client {
        return clientsDataSource.getClient(id)
    }
}
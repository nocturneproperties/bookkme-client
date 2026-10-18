package me.bookk.feature.clients.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.uuid.Uuid

internal class GetClientImpl(
    private val clientsDataSource: ClientsDataSource
) : GetClient {
    override fun flow(id: Uuid): Flow<Client?> {
        return clientsDataSource.observeClientDBChanges(id)
    }

    override suspend fun invoke(id: Uuid): Client {
        return clientsDataSource.getClient(id)
    }
}

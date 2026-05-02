package me.bookk.feature.business.domain.impl.client

import me.bookk.feature.business.domain.api.client.GetClientsList
import me.bookk.feature.business.domain.api.entity.Client
import me.bookk.feature.business.domain.datasource.ClientsDataSource
import kotlin.uuid.Uuid

internal class GetClientsListImpl(
    private val clientsDataSource: ClientsDataSource
) : GetClientsList {
    override suspend fun invoke(businessId: Uuid): List<Client> {
        return clientsDataSource.getClients(businessId).also {
            clientsDataSource.saveClientsInDb(it)
        }
    }
}
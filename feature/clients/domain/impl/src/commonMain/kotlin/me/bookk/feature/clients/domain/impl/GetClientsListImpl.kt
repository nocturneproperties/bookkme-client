package me.bookk.feature.clients.domain.impl

import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
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
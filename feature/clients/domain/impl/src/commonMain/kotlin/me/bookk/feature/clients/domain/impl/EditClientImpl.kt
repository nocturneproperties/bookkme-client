package me.bookk.feature.clients.domain.impl

import me.bookk.feature.clients.domain.api.EditClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientEvent
import me.bookk.feature.clients.domain.api.entity.clientEvents
import me.bookk.feature.clients.domain.datasource.ClientsDataSource

internal class EditClientImpl(
    private val clientsDataSource: ClientsDataSource
) : EditClient {
    override suspend fun invoke(client: Client): Client {
        return clientsDataSource.updateClient(client).also {
            clientsDataSource.saveClientsInDb(listOf(it))
            clientEvents.emit(ClientEvent.Updated(it))
        }
    }
}

package me.bookk.feature.clients.domain.impl

import me.bookk.feature.clients.domain.api.CreateClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientEvent
import me.bookk.feature.clients.domain.api.entity.clientEvents
import me.bookk.feature.clients.domain.datasource.ClientsDataSource

internal class CreateClientImpl(
    private val clientsDataSource: ClientsDataSource
) : CreateClient {
    override suspend fun invoke(client: Client): Client {
        return clientsDataSource.createClient(client).also {
            clientsDataSource.saveClientsInDb(listOf(it))
            clientEvents.emit(ClientEvent.Created(it))
        }
    }
}
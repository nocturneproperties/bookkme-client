package me.bookk.feature.clients.domain.datasource

import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.uuid.Uuid

interface ClientsDataSource {
    suspend fun getClients(businessId: Uuid): List<Client>
    suspend fun createClient(client: Client): Client
    suspend fun deleteClient(businessId: Uuid, id: Uuid)
    suspend fun saveClientsInDb(clients: List<Client>)
    suspend fun deleteClientInDb(id: Uuid)
}
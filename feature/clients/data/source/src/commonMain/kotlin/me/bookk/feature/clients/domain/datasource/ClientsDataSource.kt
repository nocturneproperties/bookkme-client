package me.bookk.feature.clients.domain.datasource

import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface ClientsDataSource {
    suspend fun getClients(businessId: Uuid): List<Client>
    suspend fun getClientsFromDb(businessId: Uuid): List<Client>
    suspend fun getClient(id: Uuid): Client
    suspend fun createClient(client: Client): Client
    suspend fun updateClient(client: Client): Client
    suspend fun deleteClient(businessId: Uuid, id: Uuid)
    suspend fun saveClientsInDb(clients: List<Client>)
    suspend fun deleteClientInDb(id: Uuid)
    suspend fun deleteClientsInDb()
    suspend fun getLastSyncedAt(businessId: Uuid): Instant?
    suspend fun saveLastSyncedAt(businessId: Uuid)
}
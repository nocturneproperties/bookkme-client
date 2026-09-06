package me.bookk.feature.clients.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface ClientsDataSource {
    suspend fun getClients(businessId: Uuid): List<Client>
    fun observeClientsDBChanges(businessId: Uuid): Flow<List<Client>>
    suspend fun getClient(id: Uuid): Client
    suspend fun createClient(client: Client): Client
    suspend fun updateClient(client: Client): Client
    suspend fun deleteClient(businessId: Uuid, id: Uuid)
    suspend fun saveClientsInDb(clients: List<Client>)
    suspend fun deleteClientInDb(id: Uuid)
    suspend fun getClientIdsInDb(businessId: Uuid): List<Uuid>
    suspend fun deleteClientsInDb(ids: List<Uuid>)
    suspend fun getLastSyncedAt(businessId: Uuid): Instant?
    suspend fun saveLastSyncedAt(businessId: Uuid)
}
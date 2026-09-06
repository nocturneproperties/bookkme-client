package me.bookk.feature.clients.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.clients.domain.api.entity.Client
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
    suspend fun deleteClientsInDb(businessId: Uuid)
}
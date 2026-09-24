package me.bookk.feature.clients.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.uuid.Uuid

internal class GetClientsListImpl(
    private val clientsDataSource: ClientsDataSource,
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges
) : GetClientsList {

    override fun flow(): Flow<List<Client>> {
        return observeDashboardBusinessChanges()
            .flatMapLatestOrNull { business ->
                clientsDataSource.observeClientsDBChanges(business.id)
            }
            .map { it.orEmpty() }
    }

    override suspend fun refresh(businessId: Uuid): List<Client> {
        val clients = clientsDataSource.getClients(businessId)
        val freshIds = clients.map { it.id }.toSet()
        val staleIds = clientsDataSource.getClientIdsInDb(businessId).filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            clientsDataSource.deleteClientsInDb(staleIds)
        }
        clientsDataSource.saveClientsInDb(clients)
        clientsDataSource.saveLastSyncedAt(businessId)
        return clients
    }
}

package me.bookk.feature.clients.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.core.domain.fetch.BusinessDataFetchAction
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.fetchPageOrRestart
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientsQuery
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.uuid.Uuid

internal class GetClientsListImpl(
    private val clientsDataSource: ClientsDataSource,
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
) : GetClientsList,
    BusinessDataFetchAction {

    override fun flow(search: String?): Flow<List<Client>> {
        val clientSearch = search?.takeIf { it.isNotBlank() }?.let(::ClientSearch)
        return observeDashboardBusinessChanges()
            .flatMapLatestOrNull { business ->
                clientsDataSource.observeClientsDBChanges(business.id)
            }
            .map { clients -> clients.orEmpty().filter { clientSearch?.matches(it) ?: true } }
    }

    override suspend fun refresh(key: ClientsQuery): Page<Client> {
        val page = clientsDataSource.getClients(key.businessId, key.search, cursor = null)
        if (key.search == null) {
            replaceCachedClients(page, key.businessId)
        } else {
            clientsDataSource.saveClientsInDb(page.items)
        }
        return page
    }

    override suspend fun loadMore(key: ClientsQuery, cursor: String): Page<Client> {
        return fetchPageOrRestart(
            fetch = { appendPage(key, cursor) },
            restart = { refresh(key) },
        )
    }

    override suspend fun doOnBusinessDataFetch(businessId: Uuid) {
        refresh(ClientsQuery(businessId))
    }

    private suspend fun appendPage(key: ClientsQuery, cursor: String): Page<Client> {
        val page = clientsDataSource.getClients(key.businessId, key.search, cursor)
        clientsDataSource.saveClientsInDb(page.items)
        return page
    }

    private suspend fun replaceCachedClients(page: Page<Client>, businessId: Uuid) {
        val freshIds = page.items.map { it.id }.toSet()
        val staleIds = clientsDataSource.getClientIdsInDb(businessId).filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            clientsDataSource.deleteClientsInDb(staleIds)
        }
        clientsDataSource.saveClientsInDb(page.items)
        clientsDataSource.saveLastSyncedAt(businessId)
    }
}

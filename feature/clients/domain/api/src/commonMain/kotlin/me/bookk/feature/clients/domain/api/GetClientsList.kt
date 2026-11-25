package me.bookk.feature.clients.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.pagination.PagedSource
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientsQuery

interface GetClientsList : PagedSource<ClientsQuery, Client> {
    fun flow(search: String? = null): Flow<List<Client>>
}

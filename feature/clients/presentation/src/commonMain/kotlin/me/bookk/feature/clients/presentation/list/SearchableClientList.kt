package me.bookk.feature.clients.presentation.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.designsystem.convenience.listPager
import me.bookk.designsystem.convenience.resetListOnChange
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientsQuery
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class SearchableClientList(
    private val viewModel: ViewModel,
    private val listState: ListState<ClientListItem>,
    private val searchField: TextFieldState,
    private val notifications: PresentationNotificationState,
    refreshState: RefreshState,
    private val getClientsList: GetClientsList,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    private val onClientClick: (Client) -> Unit,
) {
    private val searchText = MutableStateFlow("")
    private val pages = viewModel.listPager(listState, notifications, getClientsList, refreshState)

    fun start() {
        observeClients()
        loadClients()
    }

    fun search(text: String) {
        searchField.text = text
        searchText.value = text
    }

    private fun observeClients() {
        with(viewModel) {
            searchText
                .flatMapLatest { getClientsList.flow(it) }
                .flowOn(DispatcherProvider.io)
                .safeOnEach { renderClients(it) }
                .onError { notifications.add(it.notification()) }
                .observe()
        }
    }

    private fun loadClients() {
        with(viewModel) {
            observeCurrentBusinessId()
                .filterNotNull()
                .flowOn(DispatcherProvider.io)
                .resetListOnChange(listState)
                .combine(serverSearch()) { businessId, search -> ClientsQuery(businessId, search) }
                .distinctUntilChanged()
                .safeOnEach { pages.load(it) }
                .onError { notifications.add(it.notification()) }
                .observe()
        }
    }

    private fun serverSearch(): Flow<String?> {
        return searchText
            .map { text -> text.trim().takeIf { it.length >= MIN_SERVER_SEARCH_LENGTH } }
            .debounce { if (it == null) Duration.ZERO else SERVER_SEARCH_DELAY }
            .distinctUntilChanged()
    }

    private fun renderClients(clients: List<Client>) {
        if (clients.isEmpty() && listState.isInitialLoading) return
        val onClick = onClientClick
        listState.replace(
            clients.map { client ->
                ClientListItem(
                    id = client.id.toString(),
                    fullName = client.fullName,
                    onClick = { onClick(client) },
                )
            },
        )
    }

    private companion object {
        const val MIN_SERVER_SEARCH_LENGTH = 2
        val SERVER_SEARCH_DELAY = 300.milliseconds
    }
}

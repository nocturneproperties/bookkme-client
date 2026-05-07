package me.bookk.feature.clients.presentation.list

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.capitalizeChar
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientEvent
import me.bookk.feature.clients.domain.api.entity.listenFor
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.clients.presentation.list.ClientsListDestination.AddClient

class ClientsListViewModel(
    private val getClientsList: GetClientsList,
    private val args: ClientsListArgs,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createClientsListState().setup()
    private var items = listOf<ClientSection>()

    init {
        loadClients()
        listenFor<ClientEvent.Created>()
            .onEach { loadClients() }
            .retry()
            .launchIn(viewModelScope)
    }

    private fun loadClients() {
        launchCached(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.refreshState.isRefreshing = true },
            call = { getClientsList.cached(args.businessId, it) },
            onComplete = {
                val grouped = it
                    .sortedBy { it.name.trim() }
                    .groupBy { it.name[0].toString().capitalizeChar() }
                    .map { entry ->
                        ClientSection(
                            id = entry.key,
                            header = entry.key,
                            items = entry.value,
                            onItemClick = weakSelfClosure { vm, client -> vm.onClientClick(client) }
                        )
                    }
                items = grouped
                uiState.clientsList.replace(grouped)
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) },
            onTerminate = {
                uiState.refreshState.isRefreshing = false
                uiState.clientsList.isInitialLoading = false
            }
        )
    }

    private fun onClientClick(client: Client) {
        uiState.navigation.push(ClientsListDestination.ClientDetails(client.id))
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.searchField.text = query
        if (query.isBlank()) uiState.clientsList.replace(items)
        val filtered = items
            .map {
                it.copy(items = it.items.filter {
                    it.fullName.contains(query, ignoreCase = true)
                })
            }
            .filter { it.items.isNotEmpty() }
        uiState.clientsList.replace(filtered)
    }

    private fun ClientsListState.setup(): ClientsListState {
        appBar.title = ClientsRes.strings.clients_title.desc()
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = weakSelfClosure { it.uiState.navigation.push(AddClient(args.businessId)) }
                )
            )
        )
        appBar.onBackClick = weakSelfClosure { it.uiState.navigation.push(ClientsListDestination.Back) }
        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakSelfClosure { vm, value -> vm.onSearchQueryChanged(value) }
        refreshState.onRefresh = weakSelfClosure { it.loadClients() }
        clientsList.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ClientsRes.strings.clients_empty.desc()
        )
        return this
    }
}
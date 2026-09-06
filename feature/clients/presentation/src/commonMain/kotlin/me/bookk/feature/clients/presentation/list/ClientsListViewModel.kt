package me.bookk.feature.clients.presentation.list

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.capitalizeChar
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.clients.presentation.list.ClientsListDestination.AddClient

class ClientsListViewModel(
    private val getClientsList: GetClientsList,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createClientsListState().setup()
    private var items = listOf<ClientSection>()

    init {
        observeClients()
        loadClients()
    }

    private fun observeClients() {
        getClientsList.flow()
            .flowOn(DispatcherProvider.io)
            .onEach { renderClients(it) }
            .launchIn(viewModelScope)
    }

    private fun renderClients(clients: List<Client>) {
        if (clients.isEmpty() && uiState.clientsList.isInitialLoading) return
        val grouped = clients
            .sortedBy { it.name.trim() }
            .groupBy { it.name[0].toString().capitalizeChar() }
            .map { entry ->
                ClientSection(
                    id = entry.key,
                    header = entry.key,
                    items = entry.value,
                    onItemClick = weakVMClosure { vm, client -> vm.onClientClick(client) }
                )
            }
        items = grouped
        uiState.clientsList.replace(grouped)
    }

    private fun loadClients() {
        loadList(
            listState = uiState.clientsList,
            refreshState = uiState.refreshState,
            call = {
                val businessId = observeCurrentBusinessId().filterNotNull().first()
                getClientsList.refresh(businessId)
            }
        )
    }

    private fun onClientClick(client: Client) {
        uiState.navigation.push(ClientsListDestination.ClientDetails(client.id))
    }

    private fun onAddClientClick() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { observeCurrentBusinessId().filterNotNull().first() },
            onComplete = { uiState.navigation.push(AddClient(it)) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.searchField.text = query
        if (query.isBlank()) {
            uiState.clientsList.replace(items)
            return
        }
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
                    icon = DesignSystem.images.plus,
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = weakVMClosure { it.onAddClientClick() }
                )
            )
        )
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(ClientsListDestination.Back) }
        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakVMClosure { vm, value -> vm.onSearchQueryChanged(value) }
        refreshState.onRefresh = weakVMClosure { it.loadClients() }
        clientsList.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ClientsRes.strings.clients_empty.desc()
        )
        return this
    }
}

package me.bookk.feature.business.presentation.clients.list

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.capitalizeChar
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.business.domain.api.client.GetClientsList
import me.bookk.feature.business.presentation.ClientsStateFactory
import me.bookk.feature.business.presentation.clients.list.ClientsListDestination.AddClient

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
    }

    private fun loadClients() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.refreshState.isRefreshing = true },
            call = { getClientsList(args.businessId) },
            onComplete = {
                val grouped = it
                    .sortedBy { it.name.trim() }
                    .groupBy { it.name[0].toString().capitalizeChar() }
                    .map { entry ->
                        ClientSection(
                            id = entry.key,
                            header = entry.key,
                            items = entry.value
                        )
                    }
                items = grouped
                uiState.clientsList.replace(grouped)
            },
            onTerminate = {
                uiState.refreshState.isRefreshing = false
                uiState.clientsList.isInitialLoading = false
            }
        )
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
        appBar.title = BusinessRes.strings.business_clients_title.desc()
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
            label = BusinessRes.strings.business_clients_empty.desc()
        )
        return this
    }
}
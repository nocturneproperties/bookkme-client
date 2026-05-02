package me.bookk.feature.business.presentation.clients.list

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.feature.business.domain.api.client.GetClientsList
import me.bookk.feature.business.presentation.ClientsStateFactory

class ClientsListViewModel(
    private val getClientsList: GetClientsList,
    private val args: ClientsListArgs,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createClientsListState().setup()

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
                    .groupBy { it.name[0] }
                    .map { entry ->
                        ClientSection(
                            id = entry.key.toString(),
                            header = entry.key.toString(),
                            items = entry.value
                        )
                    }
                uiState.clientsList.replace(grouped)
            },
            onTerminate = { uiState.refreshState.isRefreshing = false }
        )
    }

    private fun ClientsListState.setup(): ClientsListState {
        appBar.title = BusinessRes.strings.business_clients_title.desc()
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = {}
                )
            )
        )
        searchField.placeholder = DesignSystem.strings.action_search.desc()

        return this
    }
}
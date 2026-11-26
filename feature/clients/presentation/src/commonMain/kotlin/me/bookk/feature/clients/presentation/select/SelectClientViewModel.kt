package me.bookk.feature.clients.presentation.select

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.action
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import me.bookk.feature.clients.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.clients.presentation.list.SearchableClientList
import kotlin.uuid.Uuid

class SelectClientViewModel(
    getClientsList: GetClientsList,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    private val getClientsPermissions: GetClientsPermissions,
    stateFactory: ClientsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: SelectClientState = stateFactory.createSelectClientState().setup()

    private val clients = SearchableClientList(
        viewModel = this,
        listState = uiState.clients,
        searchField = uiState.searchField,
        notifications = uiState.notifications,
        refreshState = uiState.refreshState,
        getClientsList = getClientsList,
        observeCurrentBusinessId = observeCurrentBusinessId,
        onClientClick = weakVMClosure { vm, client ->
            vm.uiState.navigation.push(SelectClientDestination.Selected(client.id))
        },
    )
    private val addAction = uiState.appBar.action(
        icon = DesignSystem.images.plus,
        contentDescription = DesignSystem.strings.action_add.desc(),
        onClick = weakVMClosure { it.onAddClientClick() },
    )

    init {
        clients.start()
        observeEditAccess()
    }

    private fun observeEditAccess() {
        observeCurrentBusinessId()
            .filterNotNull()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { loadEditAccess(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadEditAccess(businessId: Uuid) {
        launch(
            key = EDIT_ACCESS_KEY,
            launchIn = DispatcherProvider.io,
            call = { getClientsPermissions(businessId) },
            onComplete = { renderAddAction(it.canEdit) },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun renderAddAction(canEdit: Boolean) {
        uiState.appBar.actions.replace(if (canEdit) listOf(addAction) else emptyList())
    }

    private fun onAddClientClick() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { observeCurrentBusinessId().filterNotNull().first() },
            onComplete = { uiState.navigation.push(SelectClientDestination.AddClient(it)) },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun SelectClientState.setup() = apply {
        appBar.size = TopBarSize.SMALL
        appBar.title = ClientsRes.strings.clients_select_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(SelectClientDestination.Back)
        }
        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakVMClosure { vm, text -> vm.clients.search(text) }
        clients.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ClientsRes.strings.clients_empty.desc(),
        )
    }

    private companion object {
        const val EDIT_ACCESS_KEY = "select_client_edit_access"
    }
}

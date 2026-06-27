package me.bookk.feature.services.presentation.group.list

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.launchIn
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.deleteConfirmation
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.services.domain.api.group.DeleteServiceGroup
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.group.ServiceGroupEvent
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.group.listenFor
import me.bookk.feature.services.presentation.ServicesStateFactory
import kotlin.uuid.Uuid

class ServiceGroupListViewModel(
    private val businessId: Uuid,
    private val getServiceGroups: GetServiceGroups,
    private val deleteServiceGroup: DeleteServiceGroup,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: ServiceGroupListState = stateFactory.createServiceGroupListState().setup()
    private var groups = listOf<ServiceGroupListState.ServiceGroupUI>()

    init {
        loadServiceGroups()
        listenGroupEvents()
    }

    private fun loadServiceGroups() {
        launchCached(
            launchIn = DispatcherProvider.io,
            call = { getServiceGroups.cached(businessId, it) },
            onStart = { uiState.refreshState.isRefreshing = true },
            onComplete = { services ->
                groups = services.map { group ->
                    group.ui(
                        onItemClick = weakVMClosure { it.onGroupClicked(group) },
                        onDeleteClick = weakVMClosure { it.onDeleteClicked(group) }
                    )
                }
                uiState.groups.replace(groups)
            },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.refreshState.isRefreshing = false }
        )
    }

    private fun listenGroupEvents() {
        listenFor<ServiceGroupEvent> {
            loadServiceGroups()
        }.launchIn(viewModelScope)
    }

    private fun onGroupClicked(group: ServiceGroup) {
    }

    private fun onDeleteClicked(group: ServiceGroup) {
        uiState.notifications.add(
            PresentationNotification.Message.deleteConfirmation(
                onConfirmed = {
                    deleteGroup(group)
                }
            )
        )
    }

    private fun deleteGroup(group: ServiceGroup) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { deleteServiceGroup(group) },
            onStart = { uiState.refreshState.isRefreshing = true },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.refreshState.isRefreshing = false }
        )
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.search.text = query
        if (query.isBlank()) {
            uiState.groups.replace(groups)
            return
        }
        val filtered = groups
            .filter { it.name.contains(query, ignoreCase = true) }
        uiState.groups.replace(filtered)
    }

    private fun ServiceGroupListState.setup() = apply {
        appBar.title = ServicesRes.strings.services_create_groups.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(ServiceGroupListDestination.Back)
        }
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = weakVMClosure { it.uiState.isAddGroupDialogVisible = true }
                )
            )
        )

        search.placeholder = DesignSystem.strings.action_search.desc()
        search.onTextChanged = weakVMClosure { vm, query -> vm.onSearchQueryChanged(query) }

        groups.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ServicesRes.strings.service_group_empty.desc()
        )
        refreshState.onRefresh = weakVMClosure { it.loadServiceGroups() }
    }
}

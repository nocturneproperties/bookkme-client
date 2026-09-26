package me.bookk.feature.services.presentation.group.list

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.convenience.resetListOnChange
import me.bookk.designsystem.deleteConfirmation
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.services.domain.api.GetServicesPermissions
import me.bookk.feature.services.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.services.domain.api.entity.ServicesPermissions
import me.bookk.feature.services.domain.api.group.DeleteServiceGroup
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.presentation.ServicesStateFactory
import kotlin.uuid.Uuid

class ServiceGroupListViewModel(
    private val getServiceGroups: GetServiceGroups,
    private val deleteServiceGroup: DeleteServiceGroup,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    private val getServicesPermissions: GetServicesPermissions,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: ServiceGroupListState = stateFactory.createServiceGroupListState().setup()
    private var groups = listOf<ServiceGroupListState.ServiceGroupUI>()
    private var renderedGroups: List<ServiceGroup>? = null
    private var permissions: ServicesPermissions? = null

    init {
        observeGroups()
        observeBusinessChanges()
    }

    private fun observeGroups() {
        getServiceGroups.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderGroups(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderGroups(serviceGroups: List<ServiceGroup>) {
        if (serviceGroups.isEmpty() && uiState.groups.isInitialLoading) return
        renderedGroups = serviceGroups
        val canDelete = permissions?.canDelete == true
        groups = serviceGroups.map { group ->
            group.ui(
                onItemClick = weakVMClosure { it.onGroupClicked(group) },
                onDeleteClick = if (canDelete) weakVMClosure { it.onDeleteClicked(group) } else null
            )
        }
        uiState.groups.replace(groups)
    }

    private fun observeBusinessChanges() {
        observeCurrentBusinessId()
            .filterNotNull()
            .flowOn(DispatcherProvider.io)
            .resetListOnChange(uiState.groups)
            .safeOnEach {
                loadServiceGroups(it)
                loadPermissions(it)
            }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadPermissions(businessId: Uuid) {
        launch(
            key = PERMISSIONS_KEY,
            launchIn = DispatcherProvider.io,
            call = { getServicesPermissions(businessId) },
            onComplete = { renderPermissions(it) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun renderPermissions(permissions: ServicesPermissions) {
        this.permissions = permissions
        val actions = if (permissions.canEdit) {
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = weakVMClosure { it.uiState.isAddGroupDialogVisible = true }
                )
            )
        } else {
            emptyList()
        }
        uiState.appBar.actions.replace(actions)
        renderedGroups?.let { renderGroups(it) }
    }

    private fun loadServiceGroups(businessId: Uuid) {
        loadList(
            listState = uiState.groups,
            notifications = uiState.notifications,
            refreshState = uiState.refreshState,
            call = { getServiceGroups.refresh(businessId) }
        )
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

        search.placeholder = DesignSystem.strings.action_search.desc()
        search.onTextChanged = weakVMClosure { vm, query -> vm.onSearchQueryChanged(query) }

        groups.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ServicesRes.strings.service_group_empty.desc()
        )
    }

    private companion object {
        const val PERMISSIONS_KEY = "service_group_list_permissions"
    }
}

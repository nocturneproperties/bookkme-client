package me.bookk.feature.services.presentation.group.list

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.presentation.ServicesStateFactory
import kotlin.uuid.Uuid

class ServiceGroupListViewModel(
    private val businessId: Uuid,
    private val getServiceGroups: GetServiceGroups,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: ServiceGroupListState = stateFactory.createServiceGroupListState().setup()
    private var groups = listOf<ServiceGroupListState.ServiceGroupUI>()

    init {
        loadServiceGroups()
    }

    private fun loadServiceGroups() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getServiceGroups(businessId) },
            onStart = { uiState.refreshState.isRefreshing = true },
            onComplete = {
                groups = it.map { group ->
                    group.ui(weakSelfClosure { it.onGroupClicked(group) })
                }
                uiState.groups.replace(groups)
            },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.refreshState.isRefreshing = false }
        )
    }

    private fun onGroupClicked(group: ServiceGroup) {

    }

    private fun onSearchQueryChanged(query: String) {
        uiState.search.text = query
        if (query.isBlank()) uiState.groups.replace(groups)
        val filtered = groups
            .filter { it.name.contains(query, ignoreCase = true) }
        uiState.groups.replace(filtered)
    }

    private fun ServiceGroupListState.setup() = apply {
        appBar.title = ServicesRes.strings.services_create_groups.desc()
        appBar.onBackClick = weakSelfClosure {
            it.uiState.navigation.push(ServiceGroupListDestination.Back)
        }
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = weakSelfClosure {
                        it.uiState.navigation.push(ServiceGroupListDestination.AddGroup(it.businessId))
                    }
                )
            )
        )

        search.placeholder = DesignSystem.strings.action_search.desc()
        search.onTextChanged = weakSelfClosure { vm, query -> vm.onSearchQueryChanged(query) }

        groups.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ServicesRes.strings.service_group_empty.desc()
        )
        refreshState.onRefresh = weakSelfClosure { it.loadServiceGroups() }
    }
}

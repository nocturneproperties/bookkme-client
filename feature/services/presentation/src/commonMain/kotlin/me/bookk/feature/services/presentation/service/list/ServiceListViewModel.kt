package me.bookk.feature.services.presentation.service.list

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.simple.Action
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.services.presentation.service.list.ServiceListDestination.AddService
import me.bookk.feature.services.presentation.service.list.ServiceListDestination.AddServiceGroup
import me.bookk.feature.services.presentation.service.list.ServiceListDestination.Back
import me.bookk.feature.services.presentation.service.list.ServiceListDestination.ServiceDetails
import me.bookk.feature.services.presentation.service.list.ServiceListState.ServiceGroupUI
import me.bookk.feature.services.presentation.service.list.ServiceListState.ServiceUI
import kotlin.uuid.Uuid

class ServiceListViewModel(
    private val businessId: Uuid,
    private val getServices: GetServices,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: ServiceListState = stateFactory.createServiceListState().setup()

    private var items = listOf<ServiceGroupUI>()

    init {
        loadServiceList()
    }

    private fun loadServiceList() {
        launchCached(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.refreshState.isRefreshing = true },
            call = { getServices.cached(businessId, it) },
            onComplete = { services ->
                val grouped = services
                    .groupBy { it.group }
                    .map { (group, services) ->
                        ServiceGroupUI(
                            id = group.id.toString(),
                            name = group.name,
                            items = services.map { ServiceUI(it) },
                            onItemClick = weakSelfClosure { vm, item -> vm.onServiceClick(item) }
                        )
                    }
                items = grouped
                uiState.services.replace(grouped)
            },
            onTerminate = {
                uiState.refreshState.isRefreshing = false
                uiState.services.isInitialLoading = false
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onServiceClick(serviceUI: ServiceUI) {
        uiState.navigation.push(ServiceDetails(serviceUI.domain.id))
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.searchField.text = query
        if (query.isBlank()) uiState.services.replace(items)
        val filtered = items
            .map {
                it.copy(items = it.items.filter {
                    it.title.contains(query, ignoreCase = true)
                })
            }
            .filter { it.items.isNotEmpty() }
        uiState.services.replace(filtered)
    }

    private fun ServiceListState.setup() = apply {
        appBar.title = ServicesRes.strings.services_title.desc()
        appBar.onBackClick = weakSelfClosure {
            it.uiState.navigation.push(Back)
        }
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = weakSelfClosure {
                        it.uiState.navigation.push(AddService(it.businessId))
                    }
                )
            )
        )

        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakSelfClosure { vm, query -> vm.onSearchQueryChanged(query) }
        groupsSection = Action(
            title = ServicesRes.strings.services_create_groups.desc(),
            onClick = weakSelfClosure { uiState.navigation.push(AddServiceGroup(businessId))  }
        )
        services.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ServicesRes.strings.services_empty.desc()
        )
        refreshState.onRefresh = weakSelfClosure { it.loadServiceList() }
    }

}
package me.bookk.feature.services.presentation.service.list

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.presentation.ServicesStateFactory
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

    init {
        loadServiceList()
    }

    private fun loadServiceList() {
        launchCached(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.refreshState.isRefreshing = true },
            call = { getServices.cached(businessId, it) },
            onComplete = { services ->
                val items = services
                    .groupBy { it.group }
                    .map { (group, services) ->
                        ServiceGroupUI(
                            id = group.id.toString(),
                            name = group.name,
                            items = services.map { ServiceUI(it) },
                            onItemClick = weakSelfClosure { vm, item -> vm.onServiceClick(item) }
                        )
                    }
                uiState.services.replace(items)
            },
            onTerminate = {
                uiState.refreshState.isRefreshing = false
                uiState.services.isInitialLoading = false
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onServiceClick(serviceUI: ServiceUI) {

    }

    private fun ServiceListState.setup() = apply {
        appBar.title = ServicesRes.strings.services_title.desc()
        appBar.onBackClick = weakSelfClosure {
            it.uiState.navigation.push(ServiceListDestination.Back)
        }

        searchField.placeholder = DesignSystem.strings.action_search.desc()
        services.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ServicesRes.strings.services_empty.desc()
        )
        refreshState.onRefresh = weakSelfClosure { it.loadServiceList() }
    }

}
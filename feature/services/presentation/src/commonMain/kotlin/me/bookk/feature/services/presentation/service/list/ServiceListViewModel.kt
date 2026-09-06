package me.bookk.feature.services.presentation.service.list

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.deleteConfirmation
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.Action
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.services.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.services.domain.api.service.DeleteService
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.services.presentation.service.list.ServiceListDestination.AddService
import me.bookk.feature.services.presentation.service.list.ServiceListDestination.Back
import me.bookk.feature.services.presentation.service.list.ServiceListDestination.ServiceDetails
import me.bookk.feature.services.presentation.service.list.ServiceListDestination.ServiceGroups
import me.bookk.feature.services.presentation.service.list.ServiceListState.ServiceGroupUI
import me.bookk.feature.services.presentation.service.list.ServiceListState.ServiceUI

class ServiceListViewModel(
    private val getServices: GetServices,
    private val deleteService: DeleteService,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: ServiceListState = stateFactory.createServiceListState().setup()

    private var items = listOf<ServiceGroupUI>()

    init {
        observeServices()
        loadServiceList()
    }

    private fun observeServices() {
        getServices.flow()
            .flowOn(DispatcherProvider.io)
            .onEach { renderServices(it) }
            .launchIn(viewModelScope)
    }

    private fun renderServices(services: List<Service>) {
        if (services.isEmpty() && uiState.services.isInitialLoading) return
        val grouped = services
            .groupBy { it.group }
            .map { (group, groupServices) ->
                ServiceGroupUI(
                    id = group.id.toString(),
                    name = group.name,
                    items = groupServices.map { ServiceUI(it) },
                    onItemClick = weakVMClosure { vm, item -> vm.onServiceClick(item) },
                    onItemDeleteClick = weakVMClosure { vm, item -> vm.onServiceDeleteClick(item) }
                )
            }
        items = grouped
        uiState.services.replace(items)
    }

    private fun loadServiceList() {
        loadList(
            listState = uiState.services,
            refreshState = uiState.refreshState,
            call = {
                val businessId = observeCurrentBusinessId().filterNotNull().first()
                getServices.refresh(businessId)
            }
        )
    }

    private fun onServiceClick(serviceUI: ServiceUI) {
        uiState.navigation.push(ServiceDetails(serviceUI.domain.id))
    }

    private fun onServiceDeleteClick(serviceUI: ServiceUI) {
        uiState.notifications.add(
            PresentationNotification.Message.deleteConfirmation(
                onConfirmed = { deleteServiceItem(serviceUI) }
            )
        )
    }

    private fun deleteServiceItem(serviceUI: ServiceUI) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { deleteService(serviceUI.domain) },
            onStart = { uiState.refreshState.isRefreshing = true },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.refreshState.isRefreshing = false }
        )
    }

    private fun onAddServiceClick() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { observeCurrentBusinessId().filterNotNull().first() },
            onComplete = { uiState.navigation.push(AddService(it)) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.searchField.text = query
        if (query.isBlank()) {
            uiState.services.replace(items)
            return
        }
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
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(Back)
        }
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = weakVMClosure { it.onAddServiceClick() }
                )
            )
        )

        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakVMClosure { vm, query -> vm.onSearchQueryChanged(query) }
        groupsSection = Action(
            title = ServicesRes.strings.services_create_groups.desc(),
            onClick = weakVMClosure { it.uiState.navigation.push(ServiceGroups) }
        )
        services.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = ServicesRes.strings.services_empty.desc()
        )
        refreshState.onRefresh = weakVMClosure { it.loadServiceList() }
    }

}

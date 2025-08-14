package me.bookk.feature.business.presentation.bootstrap

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.business.domain.api.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.RefreshBusinessInfo
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapState.InitData
import me.bookk.feature.business.presentation.navigation.BusinessDestination

class BusinessBootstrapViewModel(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val refreshBusinessInfo: RefreshBusinessInfo,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createBootstrapState(InitData(BusinessDestination.BlockingProgress))

    init {
        loadBusiness()
    }

    private fun loadBusiness() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { refreshBusinessInfo() },
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) }
        )
        observeDashboardBusinessChanges()
            .onEach { business ->
                uiState.startDestination = if (business == null) {
                    BusinessDestination.Create
                } else {
                    BusinessDestination.Dashboard
                }
            }
            .launchIn(viewModelScope)
    }
}
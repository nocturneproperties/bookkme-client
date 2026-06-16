package me.bookk.feature.business.presentation.screen.bootstrap

import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.screen.bootstrap.BusinessBootstrapState.InitData

class BusinessBootstrapViewModel(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val refreshBusinessInfo: RefreshBusinessInfo,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createBootstrapState(InitData(BusinessDestination.BlockingProgress))

    init {
        loadBusiness()
        observeBusinessChanges()
    }

    private fun loadBusiness() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { refreshBusinessInfo() },
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) }
        )
    }

    private fun observeBusinessChanges() {
        observeDashboardBusinessChanges()
            .flowOn(DispatcherProvider.io)
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
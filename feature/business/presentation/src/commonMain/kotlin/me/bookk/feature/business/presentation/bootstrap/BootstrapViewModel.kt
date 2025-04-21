package me.bookk.feature.business.presentation.bootstrap

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.business.domain.api.ObserveBusinessChanges
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.bootstrap.BootstrapState.InitData
import me.bookk.feature.business.presentation.navigation.BusinessDestination

class BootstrapViewModel(
    private val observeBusinessChanges: ObserveBusinessChanges,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createBootstrapState(InitData(BusinessDestination.BlockingProgress))

    init {
        loadBusiness()
    }

    private fun loadBusiness() {
        observeBusinessChanges()
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
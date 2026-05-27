package me.bookk.feature.services.presentation.group.list

import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.services.presentation.ServicesStateFactory
import kotlin.uuid.Uuid

class ServiceGroupListViewModel(
    private val businessId: Uuid,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: ServiceGroupListState = stateFactory.createServiceGroupListState().setup()

    private fun ServiceGroupListState.setup() = apply {
        // TODO: Initialize UI state
        // appBar.title = ...
        // appBar.onBackClick = weakSelfClosure { it.uiState.navigation.push(ServiceGroupListDestination.Back) }
    }
}

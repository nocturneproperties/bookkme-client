package me.bookk.feature.business.presentation.dashboard

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.business.domain.api.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.dashboard.state.BusinessDashboardSection
import me.bookk.feature.business.presentation.dashboard.state.BusinessDashboardState

class BusinessDashboardViewModel(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val getAvailableDashboardFeatures: GetAvailableDashboardFeatures,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createBusinessDashboardState(createInitData())

    init {
        loadFeatures()
        observeBusiness()
    }

    private fun observeBusiness() {
        observeDashboardBusinessChanges()
            .onEach { business ->
                uiState.appBar.title = business?.name.orEmpty().desc()
            }
            .launchIn(viewModelScope)
    }

    private fun loadFeatures() {
        launch(
            launchIn = Dispatchers.IO,
            call = { getAvailableDashboardFeatures() },
            onComplete = { features ->
                val sectionList = buildList {
                    if (features.contains(DashboardFeature.BUSINESS)) {
                        add(BusinessDashboardSection.Business())
                    }
                    if (features.contains(DashboardFeature.APPOINTMENTS)) {
                        add(BusinessDashboardSection.Appointments())
                    }
                    if (features.contains(DashboardFeature.SHOP)) {
                        add(BusinessDashboardSection.Shop())
                    }
                }
                uiState.updateSections(sectionList)
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) }
        )
    }


    companion object {
        internal fun createInitData() = BusinessDashboardState.InitData()
    }
}
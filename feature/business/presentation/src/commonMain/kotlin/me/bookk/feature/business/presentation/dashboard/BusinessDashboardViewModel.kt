package me.bookk.feature.business.presentation.dashboard

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.dashboard.state.BusinessDashboardSection
import me.bookk.feature.business.presentation.dashboard.state.BusinessDashboardState
import kotlin.uuid.Uuid

class BusinessDashboardViewModel(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val getAvailableDashboardFeatures: GetAvailableDashboardFeatures,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createBusinessDashboardState(createInitData())

    init {
        observeBusiness()
        uiState.appBar.size = TopBarSize.SMALL
    }

    private fun observeBusiness() {
        observeDashboardBusinessChanges()
            .filterNotNull()
            .onEach { business ->
                uiState.appBar.title = business.name.desc()
                loadFeatures(business.id)
            }
            .flowOn(DispatcherProvider.io)
            .launchIn(viewModelScope)
    }

    private fun loadFeatures(businessId: Uuid) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getAvailableDashboardFeatures() },
            onComplete = { features ->
                val sectionList = buildList {
                    if (features.contains(DashboardFeature.BUSINESS)) {
                        add(BusinessDashboardSection.Business(businessId))
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
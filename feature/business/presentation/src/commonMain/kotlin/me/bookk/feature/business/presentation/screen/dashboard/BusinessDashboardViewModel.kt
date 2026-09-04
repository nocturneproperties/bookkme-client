package me.bookk.feature.business.presentation.screen.dashboard

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.BusinessEvent
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.entity.listenFor
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardSection
import kotlin.uuid.Uuid

class BusinessDashboardViewModel(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    private val getAvailableDashboardFeatures: GetAvailableDashboardFeatures,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createBusinessDashboardState()
    private var businessId = Uuid.random()

    init {
        observeBusiness()
        observeEvents()
    }

    private fun observeBusiness() {
        observeDashboardBusinessChanges()
            .filterNotNull()
            .flowOn(DispatcherProvider.io)
            .distinctUntilChanged()
            .onEach { business ->
                uiState.appBar.title = business.name.desc()
                businessId = business.id
                loadFeatures(business.id)
            }
            .launchIn(viewModelScope)
    }

    private fun observeEvents() {
        listenFor<BusinessEvent.PluginStateChanged> {
            loadFeatures(businessId)
        }.launchIn(viewModelScope)
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
                        add(BusinessDashboardSection.Appointments(businessId))
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
}
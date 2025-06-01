package me.bookk.feature.business.presentation.dashboard

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.business.domain.api.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.ObserveBusinessChanges
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.dashboard.state.BusinessDashboardState
import me.bookk.feature.business.presentation.dashboard.state.DashboardUIItem
import me.bookk.feature.business.presentation.dashboard.state.Section

class BusinessDashboardViewModel(
    private val observeBusinessChanges: ObserveBusinessChanges,
    private val getAvailableDashboardFeatures: GetAvailableDashboardFeatures,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createDashboardState(createInitData())

    init {
        loadFeatures()
        observeBusiness()
    }

    fun onItemClick(item: DashboardUIItem) {
        uiState.navigation.push(item.navigation)
    }

    private fun observeBusiness() {
        observeBusinessChanges()
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
                        add(Section.Business())
                    }
                    if (features.contains(DashboardFeature.APPOINTMENTS)) {
                        add(Section.Appointments())
                    }
                    if (features.contains(DashboardFeature.SHOP)) {
                        add(Section.Shop())
                    }
                }
                uiState.updateSections(sectionList)
            }
        )
    }


    companion object {
        internal fun createInitData() = BusinessDashboardState.InitData()
    }
}
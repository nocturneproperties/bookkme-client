package me.bookk.feature.dashboard.presentation

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.dashboard.presentation.state.TabItem
import me.bookk.feature.dashboard.presentation.state.TabItemsState

class DashboardViewModel(
    stateFactory: DashboardStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createDashboardState(createInitData())

    companion object {
        fun createInitData() = TabItemsState.InitData(
            selectedItemId = TabItem.Id.HOME,
            tabInitData = listOf(
                TabItem.InitData(
                    id = TabItem.Id.HOME,
                    text = DashboardRes.strings.dashboard_item_appointments.desc()
                ),
                TabItem.InitData(
                    id = TabItem.Id.BUSINESS,
                    text = DashboardRes.strings.dashboard_item_business.desc()
                ),
                TabItem.InitData(
                    id = TabItem.Id.SETTINGS,
                    text = DashboardRes.strings.dashboard_item_settings.desc()
                )
            )
        )
    }
}
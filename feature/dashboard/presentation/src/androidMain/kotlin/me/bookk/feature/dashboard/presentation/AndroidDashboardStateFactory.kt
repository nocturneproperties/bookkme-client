package me.bookk.feature.dashboard.presentation

import me.bookk.feature.dashboard.presentation.state.AndroidDashboardState
import me.bookk.feature.dashboard.presentation.state.DashboardState
import me.bookk.feature.dashboard.presentation.state.TabItemsState

class AndroidDashboardStateFactory : DashboardStateFactory {
    override fun createDashboardState(initData: TabItemsState.InitData): DashboardState {
        return AndroidDashboardState(initData)
    }
}
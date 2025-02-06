package me.bookk.feature.dashboard.presentation

import me.bookk.feature.dashboard.presentation.state.DashboardState
import me.bookk.feature.dashboard.presentation.state.TabItemsState

interface DashboardStateFactory {
    fun createDashboardState(initData: TabItemsState.InitData): DashboardState
}
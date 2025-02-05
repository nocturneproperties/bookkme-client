package me.bookk.feature.dashboard.presentation.state

import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.dashboard.presentation.DashboardNavigationDestination

interface DashboardState {
    val tabItems: TabItemsState
    val navigation: NavigationState<DashboardNavigationDestination>
}
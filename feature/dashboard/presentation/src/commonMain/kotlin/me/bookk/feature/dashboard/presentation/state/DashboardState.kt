package me.bookk.feature.dashboard.presentation.state

import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.dashboard.presentation.DashboardHomeNavigationDestination

interface DashboardState {
    val tabItems: TabItemsState
    val navigation: NavigationState<DashboardHomeNavigationDestination>
    val home: DashboardHomeState
    val notifications: PresentationNotificationState
}
package me.bookk.feature.business.presentation.screen.dashboard.state

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.business.presentation.screen.dashboard.DashboardNavigationDestination

interface BusinessDashboardState {

    val appBar: AppBarState
    val sections: List<BusinessDashboardSection>

    val notifications: PresentationNotificationState
    val navigation: NavigationState<DashboardNavigationDestination>

    fun updateSections(sections: List<BusinessDashboardSection>)

    class InitData
}
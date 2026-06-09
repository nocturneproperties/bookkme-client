package me.bookk.feature.business.presentation.dashboard

import androidx.compose.runtime.mutableStateListOf
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.business.presentation.screen.dashboard.DashboardNavigationDestination
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardSection
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardState

internal class AndroidDashboardState(
    initData: BusinessDashboardState.InitData
) : BusinessDashboardState {
    override val appBar: AppBarState = AndroidAppBarState("".desc(), size = TopBarSize.SMALL)
    override val sections = mutableStateListOf<BusinessDashboardSection>()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation = AndroidNavigationState<DashboardNavigationDestination>()

    override fun updateSections(sections: List<BusinessDashboardSection>) {
        this.sections.clear()
        this.sections.addAll(sections)
    }
}
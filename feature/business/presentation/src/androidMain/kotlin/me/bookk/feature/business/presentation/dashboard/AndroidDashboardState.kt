package me.bookk.feature.business.presentation.dashboard

import androidx.compose.runtime.mutableStateListOf
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.business.presentation.dashboard.state.BusinessDashboardState
import me.bookk.feature.business.presentation.dashboard.state.Section

internal class AndroidDashboardState(
    initData: BusinessDashboardState.InitData
) : BusinessDashboardState {
    override val appBar: AppBarState = AndroidAppBarState("".desc())
    override val sections = mutableStateListOf<Section>()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation = AndroidNavigationState<DashboardNavigationDestination>()

    override fun updateSections(sections: List<Section>) {
        this.sections.clear()
        this.sections.addAll(sections)
    }
}
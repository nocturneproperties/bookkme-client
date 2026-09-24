package me.bookk.feature.business.presentation.screen.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBusinessMenuState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BusinessMenuState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardSection
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardState

internal class AndroidDashboardState : BusinessDashboardState {
    override val appBar: AppBarState = AndroidAppBarState("".desc(), size = TopBarSize.SMALL)
    override val sections = mutableStateListOf<BusinessDashboardSection>()
    override val businessMenu: BusinessMenuState = AndroidBusinessMenuState()
    override var isCreateBusinessSheetVisible: Boolean by mutableStateOf(false)
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation = AndroidNavigationState<DashboardNavigationDestination>()

    override fun updateSections(sections: List<BusinessDashboardSection>) {
        this.sections.clear()
        this.sections.addAll(sections)
    }
}
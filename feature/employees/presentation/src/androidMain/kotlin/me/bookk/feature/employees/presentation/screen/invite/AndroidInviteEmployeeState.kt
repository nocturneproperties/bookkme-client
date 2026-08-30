package me.bookk.feature.employees.presentation.screen.invite

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidInviteEmployeeState : InviteEmployeeState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<InviteEmployeeDestinations> = AndroidNavigationState()
}

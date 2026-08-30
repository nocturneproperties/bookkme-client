package me.bookk.feature.employees.presentation.screen.invite

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface InviteEmployeeState {
    val appBar: AppBarState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<InviteEmployeeDestinations>
}

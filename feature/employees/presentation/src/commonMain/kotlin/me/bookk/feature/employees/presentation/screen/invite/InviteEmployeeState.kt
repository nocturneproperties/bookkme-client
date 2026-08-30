package me.bookk.feature.employees.presentation.screen.invite

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState

interface InviteEmployeeState {
    val appBar: AppBarState
    var descriptionText: StringDesc
    val emailField: TextFieldState
    val sendButton: ButtonState
    var invitationsHeader: StringDesc
    val invitationsList: ListState<InvitationItem>
    val refreshState: RefreshState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<InviteEmployeeDestinations>
}

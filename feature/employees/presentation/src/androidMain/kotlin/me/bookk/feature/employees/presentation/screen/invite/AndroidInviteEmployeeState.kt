package me.bookk.feature.employees.presentation.screen.invite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidRefreshState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState

internal class AndroidInviteEmployeeState : InviteEmployeeState {
    override val appBar: AppBarState = AndroidAppBarState()
    override var descriptionText: StringDesc by mutableStateOf("".desc())
    override val emailField: TextFieldState = AndroidTextFieldState()
    override val sendButton: ButtonState = AndroidButtonState()
    override var invitationsHeader: StringDesc by mutableStateOf("".desc())
    override val invitationsList: ListState<InvitationItem> = AndroidListState()
    override val refreshState: RefreshState = AndroidRefreshState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<InviteEmployeeDestinations> = AndroidNavigationState()
}

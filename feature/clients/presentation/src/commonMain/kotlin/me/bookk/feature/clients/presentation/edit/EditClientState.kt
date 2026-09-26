package me.bookk.feature.clients.presentation.edit

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface EditClientState {
    val appBar: AppBarState

    var isAttachedInfoVisible: Boolean
    var attachedInfoText: StringDesc

    val name: TextFieldState
    val lastName: TextFieldState
    val phone: TextFieldState
    val email: TextFieldState
    val description: TextFieldState

    val submit: ButtonState
    val deleteButton: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<EditClientDestination>
}

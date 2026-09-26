package me.bookk.feature.clients.presentation.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

internal class AndroidEditClientState : EditClientState {
    override val appBar: AppBarState = AndroidAppBarState()

    override var isAttachedInfoVisible: Boolean by mutableStateOf(false)
    override var attachedInfoText: StringDesc by mutableStateOf("".desc())

    override val name: TextFieldState = AndroidTextFieldState()
    override val lastName: TextFieldState = AndroidTextFieldState()
    override val phone: TextFieldState = AndroidTextFieldState()
    override val email: TextFieldState = AndroidTextFieldState()
    override val description: TextFieldState = AndroidTextFieldState()

    override val submit: ButtonState = AndroidButtonState()
    override val deleteButton: ButtonState = AndroidButtonState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<EditClientDestination> = AndroidNavigationState()
}

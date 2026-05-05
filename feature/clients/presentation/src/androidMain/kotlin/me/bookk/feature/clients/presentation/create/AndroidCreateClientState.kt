package me.bookk.feature.clients.presentation.create

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

internal class AndroidCreateClientState : CreateClientState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val name: TextFieldState = AndroidTextFieldState()
    override val lastName: TextFieldState = AndroidTextFieldState()
    override val phone: TextFieldState = AndroidTextFieldState()
    override val submit: ButtonState = AndroidButtonState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<CreateClientDestination> = AndroidNavigationState()
}
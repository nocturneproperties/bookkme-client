package me.bookk.feature.clients.presentation.create

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface CreateClientState {
    val appBar: AppBarState
    val name: TextFieldState
    val lastName: TextFieldState
    val phone: TextFieldState

    val submit: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<CreateClientDestination>
}
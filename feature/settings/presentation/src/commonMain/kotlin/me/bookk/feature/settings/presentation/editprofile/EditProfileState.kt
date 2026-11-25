package me.bookk.feature.settings.presentation.editprofile

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface EditProfileState {
    val appBar: AppBarState
    val name: TextFieldState
    val lastName: TextFieldState
    val email: TextFieldState
    val changeEmail: ButtonState
    val confirmButton: ButtonState

    val notification: PresentationNotificationState
    val navigation: NavigationState<EditProfileNavigationDestination>
}

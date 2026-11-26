package me.bookk.feature.settings.presentation.editprofile

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

internal class AndroidEditProfileState : EditProfileState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val name: TextFieldState = AndroidTextFieldState()
    override val phone: TextFieldState = AndroidTextFieldState()
    override val email: TextFieldState = AndroidTextFieldState()
    override val changeEmail: ButtonState = AndroidButtonState()
    override val confirmButton: ButtonState = AndroidButtonState()
    override val notification: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<EditProfileNavigationDestination> =
        AndroidNavigationState()
}

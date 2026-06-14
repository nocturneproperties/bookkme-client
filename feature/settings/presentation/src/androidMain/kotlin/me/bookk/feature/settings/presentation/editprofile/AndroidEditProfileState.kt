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

internal class AndroidEditProfileState(
    initData: EditProfileState.InitData
) : EditProfileState {
    override val appBar: AppBarState = AndroidAppBarState(title = initData.title)
    override val name: TextFieldState = AndroidTextFieldState(label = initData.nameHint)
    override val lastName: TextFieldState = AndroidTextFieldState(label = initData.lastNameHint)
    override val email: TextFieldState = AndroidTextFieldState(label = initData.emailHint)
    override val confirmButton: ButtonState = AndroidButtonState(
        text = initData.confirmButtonText,
        isEnabled = false
    )
    override val notification: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<EditProfileNavigationDestination> = AndroidNavigationState()
}
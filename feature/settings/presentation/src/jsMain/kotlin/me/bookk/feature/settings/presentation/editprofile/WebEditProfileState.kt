package me.bookk.feature.settings.presentation.editprofile

import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState
import me.bookk.designsystem.uistate.WebTextFieldState

internal class WebEditProfileState : EditProfileState {
    override val appBar = WebAppBarState()
    override val name = WebTextFieldState()
    override val phone = WebTextFieldState()
    override val email = WebTextFieldState()
    override val changeEmail = WebButtonState()
    override val confirmButton = WebButtonState()
    override val notification = WebNotificationState()
    override val navigation = WebNavigationState<EditProfileNavigationDestination>()
}

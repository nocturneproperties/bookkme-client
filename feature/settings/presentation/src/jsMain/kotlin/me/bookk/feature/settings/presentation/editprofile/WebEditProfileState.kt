package me.bookk.feature.settings.presentation.editprofile

import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState
import me.bookk.designsystem.uistate.WebTextFieldState

internal class WebEditProfileState(
    initData: EditProfileState.InitData,
) : EditProfileState {
    override val appBar = WebAppBarState(title = initData.title)
    override val name = WebTextFieldState(label = initData.nameHint)
    override val lastName = WebTextFieldState(label = initData.lastNameHint)
    override val confirmButton =
        WebButtonState(text = initData.confirmButtonText, isEnabled = false)
    override val notification = WebNotificationState()
    override val navigation = WebNavigationState<EditProfileNavigationDestination>()
}

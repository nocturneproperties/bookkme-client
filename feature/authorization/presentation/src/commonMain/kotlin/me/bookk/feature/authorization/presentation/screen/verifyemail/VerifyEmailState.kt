package me.bookk.feature.authorization.presentation.screen.verifyemail

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface VerifyEmailState {
    val appBar: AppBarState
    var codeSentMessage: StringDesc
    val code: TextFieldState
    val confirmButton: ButtonState
    val resendButton: ButtonState
    val changeEmailButton: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<VerifyEmailDestinations>
}

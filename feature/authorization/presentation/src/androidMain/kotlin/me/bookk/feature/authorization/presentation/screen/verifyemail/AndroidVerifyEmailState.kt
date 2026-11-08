package me.bookk.feature.authorization.presentation.screen.verifyemail

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

internal class AndroidVerifyEmailState : VerifyEmailState {
    override val appBar = AndroidAppBarState()
    override var codeSentMessage: StringDesc by mutableStateOf("".desc())
    override val code = AndroidTextFieldState()
    override val confirmButton = AndroidButtonState(isEnabled = false)
    override val resendButton = AndroidButtonState(isEnabled = false)
    override val changeEmailButton = AndroidButtonState()
    override val notifications = AndroidNotificationState()
    override val navigation = AndroidNavigationState<VerifyEmailDestinations>()
}

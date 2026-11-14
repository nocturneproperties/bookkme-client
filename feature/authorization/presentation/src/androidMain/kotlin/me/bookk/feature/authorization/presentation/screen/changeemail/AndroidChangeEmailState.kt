package me.bookk.feature.authorization.presentation.screen.changeemail

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

internal class AndroidChangeEmailState : ChangeEmailState {
    override val appBar = AndroidAppBarState()
    override var explanation: StringDesc by mutableStateOf("".desc())
    override val email = AndroidTextFieldState()
    override val continueButton = AndroidButtonState(isEnabled = false)
    override val notifications = AndroidNotificationState()
    override val navigation = AndroidNavigationState<ChangeEmailDestinations>()
}

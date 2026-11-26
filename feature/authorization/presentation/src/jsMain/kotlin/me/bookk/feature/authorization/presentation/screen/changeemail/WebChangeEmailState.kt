package me.bookk.feature.authorization.presentation.screen.changeemail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState
import me.bookk.designsystem.uistate.WebTextFieldState

internal class WebChangeEmailState : ChangeEmailState {
    override val appBar = WebAppBarState()
    override var explanation: StringDesc by mutableStateOf("".desc())
    override val email = WebTextFieldState()
    override val continueButton = WebButtonState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<ChangeEmailDestinations>()
}

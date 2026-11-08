package me.bookk.feature.authorization.presentation.screen.changeemail

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface ChangeEmailState {
    val appBar: AppBarState
    var explanation: StringDesc
    val email: TextFieldState
    val continueButton: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<ChangeEmailDestinations>
}

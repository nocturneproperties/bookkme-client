package me.bookk.feature.authorization.presentation.sign_up.state

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_up.SignUpNavigationDestination

interface SignUpState {
    val appBar: AppBarState
    val name: TextFieldState
    val lastName: TextFieldState
    val email: TextFieldState
    var passkeyInfoCardData: PasskeyInfoCardData
    val learnMoreButton: ButtonState
    val confirmButton: ButtonState
    val legalAcceptance: BooleanState
    var legalDocuments: SignUpLegalDocumentsState?
    val reloadLegalDocuments: ButtonState

    val notification: PresentationNotificationState
    val navigation: NavigationState<SignUpNavigationDestination>
}

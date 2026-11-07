package me.bookk.feature.authorization.presentation.sign_up

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentsState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

internal class AndroidSignUpState : SignUpState {
    override val appBar = AndroidAppBarState()
    override val name = AndroidTextFieldState()
    override val lastName = AndroidTextFieldState()
    override val email = AndroidTextFieldState()
    override val confirmButton = AndroidButtonState(isEnabled = false)
    override val learnMoreButton = AndroidButtonState()
    override var passkeyInfoCardData by mutableStateOf(PasskeyInfoCardData("".desc(), "".desc()))
    override val legalAcceptance = AndroidBooleanState()
    override var legalDocuments: SignUpLegalDocumentsState? by mutableStateOf(null)
    override val reloadLegalDocuments = AndroidButtonState()
    override val notification = AndroidNotificationState()
    override val navigation = AndroidNavigationState<SignUpNavigationDestination>()
}

internal class AndroidSignUpLegalDocumentState : SignUpLegalDocumentState {
    override var document: SignUpLegalDocument? by mutableStateOf(null)
    override var linkText: StringDesc by mutableStateOf("".desc())
    override var linkUrl by mutableStateOf("")
    override var onLinkClick: (() -> Unit)? by mutableStateOf(null)
}

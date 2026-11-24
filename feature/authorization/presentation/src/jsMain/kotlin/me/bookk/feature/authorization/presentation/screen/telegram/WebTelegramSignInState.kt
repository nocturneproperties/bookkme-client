package me.bookk.feature.authorization.presentation.screen.telegram

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.WebBooleanState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentsState

internal class WebTelegramSignInState : TelegramSignInState {
    override var phase: TelegramSignInPhase by mutableStateOf(TelegramSignInPhase.SIGNING_IN)
    override var message: StringDesc by mutableStateOf("".desc())
    override var legalDocuments: SignUpLegalDocumentsState? by mutableStateOf(null)
    override val legalAcceptance = WebBooleanState()
    override val continueButton = WebButtonState()
    override val retryButton = WebButtonState(isVisible = false)
}

internal class WebSignUpLegalDocumentState : SignUpLegalDocumentState {
    override var document: SignUpLegalDocument? by mutableStateOf(null)
    override var linkText: StringDesc by mutableStateOf("".desc())
    override var linkUrl: String by mutableStateOf("")
    override var onLinkClick: (() -> Unit)? by mutableStateOf(null)
}

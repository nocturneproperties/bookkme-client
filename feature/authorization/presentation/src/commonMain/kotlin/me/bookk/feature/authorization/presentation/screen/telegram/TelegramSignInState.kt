package me.bookk.feature.authorization.presentation.screen.telegram

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentsState

interface TelegramSignInState {
    var phase: TelegramSignInPhase
    var message: StringDesc
    var legalDocuments: SignUpLegalDocumentsState?
    val legalAcceptance: BooleanState
    val continueButton: ButtonState
    val retryButton: ButtonState
}

enum class TelegramSignInPhase {
    SIGNING_IN,
    LEGAL_ACCEPTANCE,
    FAILED,
}

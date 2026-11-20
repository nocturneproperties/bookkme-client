package me.bookk.feature.authorization.presentation.screen.telegram

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.test.FakeBooleanState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.feature.authorization.presentation.FakeSignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentsState

internal class FakeTelegramSignInStateFactory : TelegramSignInStateFactory {
    override fun createTelegramSignInState(): TelegramSignInState {
        return FakeTelegramSignInState()
    }

    override fun createLegalDocumentState(): SignUpLegalDocumentState {
        return FakeSignUpLegalDocumentState()
    }
}

internal class FakeTelegramSignInState : TelegramSignInState {
    override var phase: TelegramSignInPhase = TelegramSignInPhase.SIGNING_IN
    override var message: StringDesc = "".desc()
    override var legalDocuments: SignUpLegalDocumentsState? = null
    override val legalAcceptance = FakeBooleanState()
    override val continueButton = FakeButtonState()
    override val retryButton = FakeButtonState()
}

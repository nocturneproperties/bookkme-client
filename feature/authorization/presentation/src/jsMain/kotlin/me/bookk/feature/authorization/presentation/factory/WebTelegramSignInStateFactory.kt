package me.bookk.feature.authorization.presentation.factory

import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInState
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInStateFactory
import me.bookk.feature.authorization.presentation.screen.telegram.WebSignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.screen.telegram.WebTelegramSignInState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState

internal class WebTelegramSignInStateFactory : TelegramSignInStateFactory {
    override fun createTelegramSignInState(): TelegramSignInState {
        return WebTelegramSignInState()
    }

    override fun createLegalDocumentState(): SignUpLegalDocumentState {
        return WebSignUpLegalDocumentState()
    }
}

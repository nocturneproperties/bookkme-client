package me.bookk.feature.authorization.presentation.factory

import me.bookk.feature.authorization.presentation.ClientStateFactory
import me.bookk.feature.authorization.presentation.screen.clientbootstrap.ClientBootstrapState
import me.bookk.feature.authorization.presentation.screen.clientbootstrap.WebClientBootstrapState
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInState
import me.bookk.feature.authorization.presentation.screen.telegram.WebSignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.screen.telegram.WebTelegramSignInState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState

internal class WebClientStateFactory : ClientStateFactory {
    override fun createClientBootstrapState(): ClientBootstrapState {
        return WebClientBootstrapState()
    }

    override fun createTelegramSignInState(): TelegramSignInState {
        return WebTelegramSignInState()
    }

    override fun createLegalDocumentState(): SignUpLegalDocumentState {
        return WebSignUpLegalDocumentState()
    }
}

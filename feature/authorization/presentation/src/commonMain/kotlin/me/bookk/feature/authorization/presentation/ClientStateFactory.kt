package me.bookk.feature.authorization.presentation

import me.bookk.feature.authorization.presentation.screen.clientbootstrap.ClientBootstrapState
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState

interface ClientStateFactory {
    fun createClientBootstrapState(): ClientBootstrapState
    fun createTelegramSignInState(): TelegramSignInState
    fun createLegalDocumentState(): SignUpLegalDocumentState
}

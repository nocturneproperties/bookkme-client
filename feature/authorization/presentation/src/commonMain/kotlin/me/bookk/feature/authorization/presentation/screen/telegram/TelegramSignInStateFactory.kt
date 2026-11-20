package me.bookk.feature.authorization.presentation.screen.telegram

import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState

interface TelegramSignInStateFactory {
    fun createTelegramSignInState(): TelegramSignInState
    fun createLegalDocumentState(): SignUpLegalDocumentState
}

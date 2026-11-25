package me.bookk.feature.authorization.presentation

import me.bookk.feature.authorization.presentation.screen.changeemail.ChangeEmailState
import me.bookk.feature.authorization.presentation.screen.verifyemail.VerifyEmailState

interface EmailVerificationStateFactory {
    fun createVerifyEmailState(): VerifyEmailState
    fun createChangeEmailState(): ChangeEmailState
}

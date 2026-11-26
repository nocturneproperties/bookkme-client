package me.bookk.feature.authorization.presentation.factory

import me.bookk.feature.authorization.presentation.EmailVerificationStateFactory
import me.bookk.feature.authorization.presentation.screen.changeemail.ChangeEmailState
import me.bookk.feature.authorization.presentation.screen.changeemail.WebChangeEmailState
import me.bookk.feature.authorization.presentation.screen.verifyemail.VerifyEmailState
import me.bookk.feature.authorization.presentation.screen.verifyemail.WebVerifyEmailState

internal class WebEmailVerificationStateFactory : EmailVerificationStateFactory {
    override fun createVerifyEmailState(): VerifyEmailState {
        return WebVerifyEmailState()
    }

    override fun createChangeEmailState(): ChangeEmailState {
        return WebChangeEmailState()
    }
}

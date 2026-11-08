package me.bookk.feature.authorization.presentation.screen.verifyemail

import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.api.StartSignUp
import kotlin.time.Instant

sealed class VerifyEmailRequest {
    abstract val shownEmail: String
    abstract val resendAvailableAt: Instant

    class SignUp(val pendingSignUp: StartSignUp.PendingSignUp) : VerifyEmailRequest() {
        override val shownEmail: String get() = pendingSignUp.maskedEmail
        override val resendAvailableAt: Instant get() = pendingSignUp.resendAvailableAt
    }

    class EmailChange(val pendingEmailChange: StartEmailChange.PendingEmailChange) : VerifyEmailRequest() {
        override val shownEmail: String get() = pendingEmailChange.email
        override val resendAvailableAt: Instant get() = pendingEmailChange.verification.resendAvailableAt
    }
}

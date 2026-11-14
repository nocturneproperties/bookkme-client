package me.bookk.feature.authorization.domain.api

interface ResendSignUpCode {
    suspend operator fun invoke(): StartSignUp.PendingSignUp

    sealed class Error : Throwable() {
        class SignUpNotStarted : Error()
    }
}

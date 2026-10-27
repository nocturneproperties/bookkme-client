package me.bookk.feature.authorization.domain.api

interface SignIn {
    suspend operator fun invoke()

    sealed class Error : Throwable() {
        class NoCredentialsAvailable : Error()
        class NoAccountForThisPasskey : Error()
        class PasskeyVerificationFailed : Error()
    }
}
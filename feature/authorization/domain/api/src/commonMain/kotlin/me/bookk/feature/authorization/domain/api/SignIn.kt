package me.bookk.feature.authorization.domain.api

interface SignIn {
    suspend operator fun invoke()
}

interface PasskeyVerification {
    sealed class Error : Throwable() {
        data object NoCredentialsAvailable : Error()
        data object NoAccountForThisPasskey : Error()
        data object PasskeyVerificationFailed : Error()
    }
}
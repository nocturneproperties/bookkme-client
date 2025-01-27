package me.bookk.feature.authorization.data.local

import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload

interface PassKeyManager {
    suspend fun create(jsonChallenge: String): PasskeyVerificationPayload
    suspend fun authorize(jsonChallenge: String): PasskeyVerificationPayload

    sealed class Error : Exception() {
        data object UserCancelled : Error()
        data object CredentialsMissing : Error()
        data object Unknown : Error()
        data object Infrastructure : Error()
    }
}
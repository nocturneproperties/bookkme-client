package me.bookk.feature.authorization.domain.datasource.registration

interface PassKeyManager {
    suspend fun create(challenge: CreationRequest): PasskeyVerificationPayload
    suspend fun authorize(challenge: AuthorizationRequest): PasskeyVerificationPayload

    data class CreationRequest(
        val userId: String,
        val userName: String,
        val challengeJson: String,
        val challenge: String
    )

    data class AuthorizationRequest(
        val challengeJson: String,
        val challenge: String
    )

    sealed class Error(override val cause: Throwable? = null) : Exception() {
        class UserCancelled : Error()
        class CredentialsMissing : Error()
        data class Unknown(override val cause: Throwable?) : Error(cause)
        class Infrastructure : Error()
    }
}
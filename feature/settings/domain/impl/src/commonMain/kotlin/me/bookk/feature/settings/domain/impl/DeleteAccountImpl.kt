package me.bookk.feature.settings.domain.impl

import me.bookk.feature.authorization.domain.api.PasskeyVerification
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.settings.domain.api.DeleteAccount

internal class DeleteAccountImpl(
    private val authorizationDataSource: AuthorizationDataSource
) : DeleteAccount {
    override suspend fun invoke() {
        val challenge = authorizationDataSource.getAuthorizationChallenge()
        val passkeyVerification = runCatching {
            authorizationDataSource.requestPasskey(challenge)
        }.getOrElse {
            throw when(it) {
                is PasskeyVerification.Error -> DeleteAccount.Error.AccountVerificationFailed
                else -> it
            }
        }
        authorizationDataSource.deleteAccount(
            DeleteAccountRequest(
                requestId = challenge.requestId,
                publicKeyCredentialJson = passkeyVerification.jsonPayload
            )
        )
        authorizationDataSource.saveAuthorizationTokens(null)
        authorizationDataSource.setAuthorizationStatus(false)
    }
}
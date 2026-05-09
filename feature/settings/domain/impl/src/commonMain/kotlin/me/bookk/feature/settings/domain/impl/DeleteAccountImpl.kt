package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.entity.businessOrThrow
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.authorization.domain.datasource.authorization.ServerAuthenticationChallenge
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.settings.domain.api.DeleteAccount

internal class DeleteAccountImpl(
    private val authorizationDataSource: AuthorizationDataSource,
    private val passKeyManager: PassKeyManager
) : DeleteAccount {
    override suspend fun invoke() {
        val challenge = authorizationDataSource.getAuthorizationChallenge()
        val payload = authorizeWithPasskey(challenge)
        deleteAccountOnRemote(challenge, payload)
        authorizationDataSource.saveAuthorizationTokens(null)
        authorizationDataSource.setAuthorizationStatus(false)
    }

    private suspend fun authorizeWithPasskey(challenge: ServerAuthenticationChallenge): PasskeyVerificationPayload {
        return runCatching {
            passKeyManager.authorize(
                PassKeyManager.AuthorizationRequest(challenge.challengeJson, challenge.challenge)
            )
        }
            .recoverCatching {
                throw when (it) {
                    is PassKeyManager.Error.UserCancelled -> Error.Ignore(it)
                    is PassKeyManager.Error.CredentialsMissing -> DeleteAccount.Error.AccountVerificationFailed()
                    else -> DeleteAccount.Error.AccountVerificationFailed()
                }
            }.getOrThrow()
    }

    private suspend fun deleteAccountOnRemote(
        challenge: ServerAuthenticationChallenge,
        payload: PasskeyVerificationPayload
    ) {
        runCatching {
            authorizationDataSource.deleteAccount(
                DeleteAccountRequest(
                    requestId = challenge.requestId,
                    publicKeyCredentialJson = payload.jsonPayload
                )
            )
        }.getOrElse {
            when (it.businessOrThrow().errorCode) {
                AuthErrorCodes.VERIFICATION_FAILED -> DeleteAccount.Error.AccountVerificationFailed()
                else -> it
            }
        }
    }
}
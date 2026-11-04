package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.entity.ActionConfirmation
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.settings.domain.api.DeleteAccount
import kotlin.uuid.Uuid

internal class DeleteAccountImpl(
    private val authorizationDataSource: AuthorizationDataSource,
    private val passKeyManager: PassKeyManager,
    private val logOut: LogOut,
    private val confirmation: ConfirmSensitiveAction,
    private val profile: UserProfileCRUD,
) : DeleteAccount {
    override suspend fun invoke() {
        val confirmedAction = confirmAccountDeletion()
        deleteAccountOnRemote(confirmedAction.token)
        signalAccountDeleted(confirmedAction.publicKeyCredentialJson)
        clearSession()
    }

    private suspend fun confirmAccountDeletion(): ActionConfirmation {
        val userId = profile.get().id
        return runCatching {
            confirmation(
                SensitiveAction.DELETE_ACCOUNT,
                Uuid.NIL,
                userId,
            )
        }
            .getOrElse {
                throw when (it) {
                    is ConfirmSensitiveAction.Error ->
                        DeleteAccount.Error.AccountVerificationFailed()

                    else -> it
                }
            }
    }

    private suspend fun deleteAccountOnRemote(confirmationToken: String) {
        runCatching {
            authorizationDataSource.deleteAccount(DeleteAccountRequest(confirmationToken))
        }
            .onBusinessError {
                when (it.errorCode) {
                    AuthErrorCodes.ACTION_CONFIRMATION_INVALID,
                    AuthErrorCodes.VERIFICATION_FAILED,
                    -> throw DeleteAccount.Error.AccountVerificationFailed()
                }
            }
            .getOrThrow()
    }

    private suspend fun signalAccountDeleted(assertionJson: String) {
        runCatching {
            passKeyManager.signalAccountDeleted(PasskeyVerificationPayload(assertionJson))
        }
    }

    private suspend fun clearSession() {
        logOut()
        authorizationDataSource.saveAuthorizationTokens(null)
        authorizationDataSource.setAuthorizationStatus(false)
    }
}

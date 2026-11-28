package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.authorization.domain.datasource.deletion.AccountDeletionDataSource
import me.bookk.feature.settings.domain.api.RequestAccountDeletion
import kotlin.time.Instant

internal class AccountDeletionScheduler(
    private val authorizationDataSource: AuthorizationDataSource,
    private val accountDeletionDataSource: AccountDeletionDataSource,
) {
    suspend fun scheduleDeletion(confirmationToken: String) {
        val deletesAt = requestDeletion(confirmationToken)
        accountDeletionDataSource.saveScheduledDeletion(deletesAt)
    }

    private suspend fun requestDeletion(confirmationToken: String): Instant {
        return runCatching {
            authorizationDataSource.requestAccountDeletion(DeleteAccountRequest(confirmationToken))
        }
            .onBusinessError {
                when (it.errorCode) {
                    AuthErrorCodes.ACTION_CONFIRMATION_INVALID,
                    AuthErrorCodes.VERIFICATION_FAILED,
                    -> {
                        throw RequestAccountDeletion.Error.AccountVerificationFailed()
                    }

                    AuthErrorCodes.ACCOUNT_DELETION_ON_HOLD -> {
                        throw RequestAccountDeletion.Error.DeletionOnHold()
                    }
                }
            }
            .getOrThrow()
    }
}

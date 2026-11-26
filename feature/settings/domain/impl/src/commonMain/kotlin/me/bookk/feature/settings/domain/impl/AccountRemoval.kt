package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.settings.domain.api.DeleteAccount

internal class AccountRemoval(
    private val authorizationDataSource: AuthorizationDataSource,
) {
    suspend fun deleteOnRemote(confirmationToken: String) {
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
}

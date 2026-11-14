package me.bookk.feature.authorization.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.ConfirmEmailChange
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.email.EmailChangeDataSource
import me.bookk.feature.authorization.domain.datasource.profile.UserProfileDataSource

internal class ConfirmEmailChangeImpl(
    private val emailChangeDataSource: EmailChangeDataSource,
    private val userProfileDataSource: UserProfileDataSource,
) : ConfirmEmailChange {

    override suspend fun invoke(change: StartEmailChange.PendingEmailChange, code: String) {
        confirm(change, code)
        storeConfirmedEmail(change.email)
    }

    private suspend fun confirm(change: StartEmailChange.PendingEmailChange, code: String) {
        runCatching { emailChangeDataSource.confirmEmailChange(change.verification.id, code) }
            .onBusinessError { error ->
                throw error.toEmailVerificationError() ?: when (error.errorCode) {
                    AuthErrorCodes.EMAIL_EXIST -> ConfirmEmailChange.Error.EmailAlreadyExist()
                    else -> error
                }
            }
            .getOrThrow()
    }

    private suspend fun storeConfirmedEmail(email: String) {
        val profile = userProfileDataSource.getProfileFromDatabase() ?: return
        userProfileDataSource.upsertProfile(profile.copy(email = email))
    }
}

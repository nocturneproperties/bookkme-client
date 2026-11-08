package me.bookk.feature.authorization.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.email.EmailChangeDataSource

internal class StartEmailChangeImpl(
    private val emailChangeDataSource: EmailChangeDataSource,
) : StartEmailChange {

    override suspend fun invoke(email: String): StartEmailChange.PendingEmailChange {
        val trimmedEmail = email.trim()
        return runCatching { emailChangeDataSource.startEmailChange(trimmedEmail) }
            .onBusinessError { error ->
                throw error.toEmailVerificationError() ?: when (error.errorCode) {
                    AuthErrorCodes.EMAIL_EXIST -> StartEmailChange.Error.EmailAlreadyExist()
                    AuthErrorCodes.INVALID_EMAIL_FORMAT -> StartEmailChange.Error.InvalidEmailFormat()
                    else -> error
                }
            }
            .map { StartEmailChange.PendingEmailChange(trimmedEmail, it) }
            .getOrThrow()
    }
}

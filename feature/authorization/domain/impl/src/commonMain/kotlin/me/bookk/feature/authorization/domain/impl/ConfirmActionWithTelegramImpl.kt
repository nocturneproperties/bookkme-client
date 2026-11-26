package me.bookk.feature.authorization.domain.impl

import me.bookk.core.coroutine.runSuspendCatching
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.ConfirmActionWithTelegram
import me.bookk.feature.authorization.domain.api.ConfirmActionWithTelegram.Error
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource

internal class ConfirmActionWithTelegramImpl(
    private val dataSource: AuthorizationDataSource,
) : ConfirmActionWithTelegram {
    override suspend fun invoke(initData: String): String {
        return runSuspendCatching { dataSource.confirmActionWithTelegram(initData) }
            .onBusinessError {
                when (it.errorCode) {
                    AuthErrorCodes.TELEGRAM_CONFIRMATION_EXPIRED -> {
                        throw Error.ConfirmationExpired()
                    }

                    AuthErrorCodes.TELEGRAM_ACCOUNT_MISMATCH -> {
                        throw Error.TelegramAccountMismatch()
                    }

                    AuthErrorCodes.TELEGRAM_DATA_INVALID,
                    AuthErrorCodes.TELEGRAM_DATA_EXPIRED,
                    -> {
                        throw Error.TelegramDataInvalid()
                    }
                }
            }
            .getOrThrow()
    }
}

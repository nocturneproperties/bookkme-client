package me.bookk.feature.authorization.domain.impl

import me.bookk.core.coroutine.runSuspendCatching
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.StartTelegramActionConfirmation
import me.bookk.feature.authorization.domain.api.StartTelegramActionConfirmation.Error
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.TelegramActionConfirmationStart
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.uuid.Uuid

internal class StartTelegramActionConfirmationImpl(
    private val dataSource: AuthorizationDataSource,
) : StartTelegramActionConfirmation {
    override suspend fun invoke(action: SensitiveAction, businessId: Uuid, targetId: Uuid) {
        runSuspendCatching {
            dataSource.startTelegramActionConfirmation(
                TelegramActionConfirmationStart(action, businessId, targetId),
            )
        }
            .onBusinessError {
                when (it.errorCode) {
                    AuthErrorCodes.TELEGRAM_CHAT_UNREACHABLE -> throw Error.BotCannotWrite()
                    AuthErrorCodes.TELEGRAM_CONFIRMATION_TOO_FREQUENT -> throw Error.TooFrequent()
                }
            }
            .getOrThrow()
    }
}

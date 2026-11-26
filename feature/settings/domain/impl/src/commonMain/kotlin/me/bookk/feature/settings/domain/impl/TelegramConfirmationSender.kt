package me.bookk.feature.settings.domain.impl

import me.bookk.core.coroutine.runSuspendCatching
import me.bookk.feature.authorization.domain.api.StartTelegramActionConfirmation
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.settings.domain.api.entity.ActionOutcome
import me.bookk.feature.settings.domain.api.entity.ChatConfirmationError
import kotlin.uuid.Uuid

internal class TelegramConfirmationSender(
    private val startConfirmation: StartTelegramActionConfirmation,
    private val profile: UserProfileCRUD,
) {
    suspend fun send(action: SensitiveAction): ActionOutcome<Nothing> {
        val userId = profile.get().id
        runSuspendCatching { startConfirmation(action, Uuid.NIL, userId) }
            .getOrElse { throw it.toChatConfirmationError() }
        return ActionOutcome.ConfirmationSent
    }

    private fun Throwable.toChatConfirmationError(): Throwable {
        return when (this) {
            is StartTelegramActionConfirmation.Error.BotCannotWrite -> {
                ChatConfirmationError.WriteAccessRequired()
            }

            is StartTelegramActionConfirmation.Error.TooFrequent -> {
                ChatConfirmationError.TooFrequent()
            }

            else -> {
                this
            }
        }
    }
}

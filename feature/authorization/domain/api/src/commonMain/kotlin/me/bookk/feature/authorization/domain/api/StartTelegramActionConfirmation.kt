package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.uuid.Uuid

interface StartTelegramActionConfirmation {
    suspend operator fun invoke(action: SensitiveAction, businessId: Uuid, targetId: Uuid)

    sealed interface Error {
        class BotCannotWrite :
            Exception(),
            Error
        class TooFrequent :
            Exception(),
            Error
    }
}

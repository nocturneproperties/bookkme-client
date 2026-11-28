package me.bookk.feature.settings.domain.api

import me.bookk.core.domain.launch.ConfirmableAction

interface CompleteTelegramConfirmation {
    suspend operator fun invoke(action: ConfirmableAction, initData: String)

    sealed interface Error {
        class ConfirmationExpired :
            Exception(),
            Error
        class TelegramAccountMismatch :
            Exception(),
            Error
        class TelegramDataInvalid :
            Exception(),
            Error
        class ExportTooFrequent :
            Exception(),
            Error
        class DeletionOnHold :
            Exception(),
            Error
    }
}

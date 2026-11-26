package me.bookk.feature.authorization.domain.api

interface ConfirmActionWithTelegram {
    suspend operator fun invoke(initData: String): String

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
    }
}

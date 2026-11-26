package me.bookk.feature.settings.domain.api.entity

sealed interface ChatConfirmationError {
    class WriteAccessRequired :
        Exception(),
        ChatConfirmationError
    class TooFrequent :
        Exception(),
        ChatConfirmationError
}

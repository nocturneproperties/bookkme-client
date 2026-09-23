package me.bookk.core.presentation.error

interface ErrorMapper {
    fun mapToNotification(e: Throwable): PresentationNotification
    fun mapToDescription(e: Throwable): ErrorDescription
}

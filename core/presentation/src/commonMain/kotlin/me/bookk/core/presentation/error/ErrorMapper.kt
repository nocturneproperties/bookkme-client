package me.bookk.core.presentation.error

interface ErrorMapper {
    fun mapToError(e: Throwable): PresentationError
}
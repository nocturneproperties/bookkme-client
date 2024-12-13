package me.bookk.core.presentation.error

sealed interface PresentationError {
    data object Unauthorized : PresentationError
    class Unsupported(val message: String?) : PresentationError
}
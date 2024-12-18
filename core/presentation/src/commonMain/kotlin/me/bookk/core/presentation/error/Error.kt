package me.bookk.core.presentation.error

sealed interface PresentationError {
    data object Unauthorized : PresentationError
    data object NoConnection : PresentationError
    data object ServerError : PresentationError
    class Unsupported(val message: String?) : PresentationError
    class Message(val message: String?) : PresentationError
}
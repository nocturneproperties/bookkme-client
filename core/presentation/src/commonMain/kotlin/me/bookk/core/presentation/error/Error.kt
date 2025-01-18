package me.bookk.core.presentation.error

import dev.icerock.moko.resources.desc.StringDesc

sealed interface PresentationError {
    data object NoConnection : PresentationError
    data object ServerError : PresentationError
    data object Unsupported : PresentationError
    data object Ignore : PresentationError
    class Message(val message: StringDesc) : PresentationError
}
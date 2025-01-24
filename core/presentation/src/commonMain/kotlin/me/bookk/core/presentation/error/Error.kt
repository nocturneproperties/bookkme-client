package me.bookk.core.presentation.error

import dev.icerock.moko.resources.desc.StringDesc

sealed interface PresentationError {
    data object Ignore : PresentationError
    data class Message(
        val title: StringDesc? = null,
        val message: StringDesc,
        val buttonText: StringDesc
    ) : PresentationError
}
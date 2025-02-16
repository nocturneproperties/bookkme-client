package me.bookk.core.presentation.error

import dev.icerock.moko.resources.desc.StringDesc

sealed interface PresentationNotification {
    data object Ignore : PresentationNotification
    data class Message(
        val title: StringDesc? = null,
        val message: StringDesc,
        val buttonText: StringDesc
    ) : PresentationNotification
}
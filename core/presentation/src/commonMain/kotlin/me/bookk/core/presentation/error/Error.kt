package me.bookk.core.presentation.error

import dev.icerock.moko.resources.desc.StringDesc

sealed interface PresentationNotification {
    data object Ignore : PresentationNotification
    data object Unauthorized : PresentationNotification
    //Message that displayed on top of everything even if you navigate between screens
    data class GlobalMessage(
        val text: StringDesc
    ) : PresentationNotification
    data class Message(
        val title: StringDesc? = null,
        val message: StringDesc,
        val buttonText: StringDesc
    ) : PresentationNotification
}
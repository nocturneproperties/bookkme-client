package me.bookk.designsystem.uistate

import me.bookk.core.presentation.error.PresentationNotification

interface PresentationNotificationState {
    //Immutable List is better here because of ObjC/Kotlin interoperability
    val presentationNotification: List<PresentationNotification>

    fun add(notification: PresentationNotification)
    fun removeFirst()
}
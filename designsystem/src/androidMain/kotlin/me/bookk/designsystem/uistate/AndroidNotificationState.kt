package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import me.bookk.core.presentation.error.PresentationNotification

@Immutable
class AndroidNotificationState : PresentationNotificationState {
    override val presentationNotification: MutableList<PresentationNotification> = mutableStateListOf()

    override fun add(notification: PresentationNotification) {
        presentationNotification.add(notification)
    }

    override fun removeFirst() {
        if (presentationNotification.isEmpty()) return
        presentationNotification.removeAt(0)
    }
}
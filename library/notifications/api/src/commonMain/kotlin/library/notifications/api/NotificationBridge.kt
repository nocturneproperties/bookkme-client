package library.notifications.api

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object NotificationBridge : KoinComponent {

    private val notificationManager: LocalNotificationManager by inject()

    fun showNotification(notification: LocalNotification) {
        notificationManager.show(notification)
    }
}
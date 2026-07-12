package library.notifications.api

interface LocalNotificationManager {
    fun show(notification: LocalNotification)
    fun cancel(id: String)
    fun cancelAll()
}

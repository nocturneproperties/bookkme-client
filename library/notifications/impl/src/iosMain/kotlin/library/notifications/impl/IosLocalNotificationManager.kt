package library.notifications.impl

import library.notifications.api.LocalNotification
import library.notifications.api.LocalNotificationManager
import me.bookk.core.Logger
import platform.Foundation.NSNumber
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class IosLocalNotificationManager : LocalNotificationManager {

    private val logger = Logger.create("LocalNotificationManager")
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override fun show(notification: LocalNotification) {
        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.body)
            setSound(UNNotificationSound.defaultSound())
            notification.group?.let { setThreadIdentifier(it) }
            notification.subtitle?.let { setSubtitle(it) }
            notification.badgeCount?.let { setBadge(NSNumber(int = it)) }
            if (notification.data.isNotEmpty()) {
                @Suppress("UNCHECKED_CAST")
                setUserInfo(notification.data as Map<Any?, *>)
            }
        }

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = notification.id,
            content = content,
            trigger = null
        )

        notificationCenter.addNotificationRequest(request) { error ->
            error?.let { logger.e(Exception(it.localizedDescription)) }
        }
    }

    override fun cancel(id: String) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(id))
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(id))
    }

    override fun cancelAll() {
        notificationCenter.removeAllPendingNotificationRequests()
        notificationCenter.removeAllDeliveredNotifications()
    }
}

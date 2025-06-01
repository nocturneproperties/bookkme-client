package library.permissions.impl.checker

import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal actual class NotificationPermissionChecker : PermissionChecker {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override val type: PermissionType = PermissionType.NOTIFICATIONS

    override suspend fun requestPermission(): Boolean = suspendCoroutine {
        notificationCenter
            .requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or
                        UNAuthorizationOptionSound or
                        UNAuthorizationOptionBadge
            ) { result, _ ->
                it.resume(result)
            }
    }

    override suspend fun hasPermission(): Boolean = suspendCoroutine {
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            it.resume(settings?.authorizationStatus == UNAuthorizationStatusAuthorized)
        }
    }
}
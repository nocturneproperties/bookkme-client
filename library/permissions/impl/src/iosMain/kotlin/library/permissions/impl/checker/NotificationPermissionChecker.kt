package library.permissions.impl.checker

import kotlinx.coroutines.suspendCancellableCoroutine
import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

internal actual class NotificationPermissionChecker : PermissionChecker {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    actual override val type: PermissionType = PermissionType.NOTIFICATIONS

    actual override suspend fun requestPermission(): Boolean = suspendCancellableCoroutine {
        notificationCenter
            .requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or
                        UNAuthorizationOptionSound or
                        UNAuthorizationOptionBadge
            ) { hasPermissions, _ ->
                if (hasPermissions) {
                    UIApplication.sharedApplication.registerForRemoteNotifications()
                }
                it.resume(hasPermissions)
            }
    }

    actual override suspend fun hasPermission(): Boolean = suspendCancellableCoroutine {
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            it.resume(settings?.authorizationStatus == UNAuthorizationStatusAuthorized)
        }
    }
}
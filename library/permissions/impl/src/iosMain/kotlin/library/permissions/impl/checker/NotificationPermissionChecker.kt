package library.permissions.impl.checker

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType
import me.bookk.core.coroutine.DispatcherProvider
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

internal class NotificationPermissionChecker : PermissionChecker {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override val type: PermissionType = PermissionType.NOTIFICATIONS

    override suspend fun requestPermission(): Boolean {
        val hasPermissions = requestAuthorization()
        if (hasPermissions) {
            withContext(DispatcherProvider.main) {
                UIApplication.sharedApplication.registerForRemoteNotifications()
            }
        }
        return hasPermissions
    }

    private suspend fun requestAuthorization(): Boolean = suspendCancellableCoroutine {
        notificationCenter
            .requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or
                    UNAuthorizationOptionSound or
                    UNAuthorizationOptionBadge,
            ) { hasPermissions, _ ->
                it.resume(hasPermissions)
            }
    }

    override suspend fun hasPermission(): Boolean = suspendCancellableCoroutine {
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            it.resume(settings?.authorizationStatus == UNAuthorizationStatusAuthorized)
        }
    }
}

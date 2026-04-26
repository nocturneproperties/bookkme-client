package library.permissions.impl.checker

import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType

internal expect class NotificationPermissionChecker : PermissionChecker {
    override val type: PermissionType
    override suspend fun requestPermission(): Boolean
    override suspend fun hasPermission(): Boolean
}
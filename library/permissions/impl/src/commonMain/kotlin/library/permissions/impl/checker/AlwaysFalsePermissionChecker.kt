package library.permissions.impl.checker

import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType

internal class AlwaysFalsePermissionChecker : PermissionChecker {
    override val type: PermissionType = PermissionType.NONE

    override suspend fun requestPermission(): Boolean {
        return false
    }

    override suspend fun hasPermission(): Boolean {
        return false
    }
}
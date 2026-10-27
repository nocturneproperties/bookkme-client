package library.permissions.api

interface PermissionChecker {
    val type: PermissionType

    suspend fun requestPermission(): Boolean
    suspend fun hasPermission(): Boolean
}
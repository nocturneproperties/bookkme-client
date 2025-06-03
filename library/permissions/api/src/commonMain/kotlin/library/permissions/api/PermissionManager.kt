package library.permissions.api

class PermissionManager(
    private val checkers: Map<PermissionType, PermissionChecker>,
    private val fallbackChecker: PermissionChecker
) {
    suspend fun requestPermission(type: PermissionType): Boolean {
        return checkers.getOrElse(type, ::fallbackChecker).requestPermission()
    }

    suspend fun hasPermission(type: PermissionType): Boolean {
        return checkers.getOrElse(type, ::fallbackChecker).hasPermission()
    }
}
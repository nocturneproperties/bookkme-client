package library.permissions.api

class PermissionManager(
    private val checkers: Map<PermissionType, PermissionChecker>,
    private val fallbackChecker: PermissionChecker,
) {
    fun supports(type: PermissionType): Boolean {
        return type in checkers
    }

    suspend fun requestPermission(type: PermissionType): Boolean {
        return checkers.getOrElse(type, ::fallbackChecker).requestPermission()
    }

    suspend fun hasPermission(type: PermissionType): Boolean {
        return checkers.getOrElse(type, ::fallbackChecker).hasPermission()
    }
}

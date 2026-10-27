package library.permissions.impl.checker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType

internal actual class NotificationPermissionChecker : AndroidPermissionChecker(),
    PermissionChecker {

    actual override val type: PermissionType = PermissionType.NOTIFICATIONS

    actual override suspend fun requestPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            check(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }
    }

    actual override suspend fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                awaitActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
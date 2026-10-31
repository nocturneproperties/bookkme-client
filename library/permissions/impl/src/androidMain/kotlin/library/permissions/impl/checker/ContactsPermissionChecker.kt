package library.permissions.impl.checker

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType

internal actual class ContactsPermissionChecker : AndroidPermissionChecker(), PermissionChecker {

    actual override val type: PermissionType = PermissionType.CONTACTS

    actual override suspend fun requestPermission(): Boolean {
        return check(Manifest.permission.READ_CONTACTS)
    }

    actual override suspend fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            awaitActivity(),
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }
}

package library.permissions.impl.checker

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType

internal class ContactsPermissionChecker :
    AndroidPermissionChecker(),
    PermissionChecker {

    override val type: PermissionType = PermissionType.CONTACTS

    override suspend fun requestPermission(): Boolean {
        return check(Manifest.permission.READ_CONTACTS)
    }

    override suspend fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            awaitActivity(),
            Manifest.permission.READ_CONTACTS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}

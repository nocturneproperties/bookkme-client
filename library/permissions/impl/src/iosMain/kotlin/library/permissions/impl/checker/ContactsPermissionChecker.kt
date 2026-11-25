package library.permissions.impl.checker

import kotlinx.coroutines.suspendCancellableCoroutine
import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType
import platform.Contacts.CNAuthorizationStatusAuthorized
import platform.Contacts.CNAuthorizationStatusLimited
import platform.Contacts.CNContactStore
import platform.Contacts.CNEntityType
import kotlin.coroutines.resume

internal class ContactsPermissionChecker : PermissionChecker {

    override val type: PermissionType = PermissionType.CONTACTS

    override suspend fun requestPermission(): Boolean = suspendCancellableCoroutine {
        CNContactStore().requestAccessForEntityType(CNEntityType.CNEntityTypeContacts) {
                granted,
                _,
            ->
            it.resume(granted)
        }
    }

    override suspend fun hasPermission(): Boolean {
        val status = CNContactStore.authorizationStatusForEntityType(
            CNEntityType.CNEntityTypeContacts,
        )
        return status == CNAuthorizationStatusAuthorized || status == CNAuthorizationStatusLimited
    }
}

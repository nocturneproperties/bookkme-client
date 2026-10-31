package me.bookk.feature.clients.data.datasource

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.datasource.DeviceContactsDataSource
import platform.Contacts.CNContact
import platform.Contacts.CNContactEmailAddressesKey
import platform.Contacts.CNContactFamilyNameKey
import platform.Contacts.CNContactFetchRequest
import platform.Contacts.CNContactGivenNameKey
import platform.Contacts.CNContactOrganizationNameKey
import platform.Contacts.CNContactPhoneNumbersKey
import platform.Contacts.CNContactStore
import platform.Contacts.CNLabeledValue
import platform.Contacts.CNPhoneNumber
import platform.Foundation.NSError

internal class IosDeviceContactsDataSource : DeviceContactsDataSource {

    override suspend fun getContacts(): List<ClientImportEntry> {
        return fetchContacts().map { it.toEntry() }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun fetchContacts(): List<CNContact> {
        val contacts = mutableListOf<CNContact>()
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val isFetched = CNContactStore().enumerateContactsWithFetchRequest(
                CNContactFetchRequest(keysToFetch = KEYS_TO_FETCH),
                error.ptr
            ) { contact, _ -> contact?.let(contacts::add) }
            check(isFetched) { error.value?.localizedDescription.orEmpty() }
        }
        return contacts
    }

    private fun CNContact.toEntry(): ClientImportEntry {
        return ClientImportEntry(
            name = givenName.ifBlank { organizationName },
            lastName = familyName,
            phone = phoneNumbers.labeledValues<CNPhoneNumber>().map { it.stringValue }.firstOrNull(String::isNotBlank),
            email = emailAddresses.labeledValues<String>().firstOrNull(String::isNotBlank)
        )
    }

    private inline fun <reified T> List<*>.labeledValues(): List<T> {
        return filterIsInstance<CNLabeledValue>().mapNotNull { it.value as? T }
    }

    private companion object {
        val KEYS_TO_FETCH = listOf(
            CNContactGivenNameKey,
            CNContactFamilyNameKey,
            CNContactOrganizationNameKey,
            CNContactPhoneNumbersKey,
            CNContactEmailAddressesKey
        )
    }
}

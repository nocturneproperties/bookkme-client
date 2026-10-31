package me.bookk.feature.clients.data.datasource

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.Data
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.datasource.DeviceContactsDataSource

internal class AndroidDeviceContactsDataSource(
    private val context: Context
) : DeviceContactsDataSource {

    override suspend fun getContacts(): List<ClientImportEntry> {
        return queryContactRows()
            .groupBy { it.contactId }
            .values
            .map { it.toEntry() }
    }

    private fun queryContactRows(): List<ContactRow> {
        val cursor = context.contentResolver.query(
            Data.CONTENT_URI,
            PROJECTION,
            "${Data.MIMETYPE} IN (?, ?, ?)",
            arrayOf(StructuredName.CONTENT_ITEM_TYPE, Phone.CONTENT_ITEM_TYPE, Email.CONTENT_ITEM_TYPE),
            "${Data.IS_PRIMARY} DESC"
        ) ?: return emptyList()
        return cursor.use {
            generateSequence { cursor.takeIf { it.moveToNext() }?.toContactRow() }.toList()
        }
    }

    private fun Cursor.toContactRow(): ContactRow {
        return ContactRow(
            contactId = getLong(getColumnIndexOrThrow(Data.CONTACT_ID)),
            displayName = string(Data.DISPLAY_NAME_PRIMARY),
            detail = when (string(Data.MIMETYPE)) {
                StructuredName.CONTENT_ITEM_TYPE -> ContactDetail.Name(
                    given = string(StructuredName.GIVEN_NAME),
                    family = string(StructuredName.FAMILY_NAME)
                )
                Phone.CONTENT_ITEM_TYPE -> ContactDetail.PhoneNumber(
                    number = string(Phone.NORMALIZED_NUMBER) ?: string(Phone.NUMBER)
                )
                Email.CONTENT_ITEM_TYPE -> ContactDetail.EmailAddress(address = string(Email.ADDRESS))
                else -> null
            }
        )
    }

    private fun Cursor.string(column: String): String? {
        return getString(getColumnIndexOrThrow(column))?.takeIf { it.isNotBlank() }
    }

    private fun List<ContactRow>.toEntry(): ClientImportEntry {
        val details = mapNotNull { it.detail }
        val names = details.filterIsInstance<ContactDetail.Name>()
        return ClientImportEntry(
            name = names.firstNotNullOfOrNull { it.given } ?: firstNotNullOfOrNull { it.displayName }.orEmpty(),
            lastName = names.firstNotNullOfOrNull { it.family }.orEmpty(),
            phone = details.filterIsInstance<ContactDetail.PhoneNumber>().firstNotNullOfOrNull { it.number },
            email = details.filterIsInstance<ContactDetail.EmailAddress>().firstNotNullOfOrNull { it.address }
        )
    }

    private class ContactRow(
        val contactId: Long,
        val displayName: String?,
        val detail: ContactDetail?
    )

    private sealed interface ContactDetail {
        class Name(val given: String?, val family: String?) : ContactDetail
        class PhoneNumber(val number: String?) : ContactDetail
        class EmailAddress(val address: String?) : ContactDetail
    }

    private companion object {
        val PROJECTION = arrayOf(
            Data.CONTACT_ID,
            Data.MIMETYPE,
            Data.DISPLAY_NAME_PRIMARY,
            StructuredName.GIVEN_NAME,
            StructuredName.FAMILY_NAME,
            Phone.NUMBER,
            Phone.NORMALIZED_NUMBER,
            Email.ADDRESS
        )
    }
}

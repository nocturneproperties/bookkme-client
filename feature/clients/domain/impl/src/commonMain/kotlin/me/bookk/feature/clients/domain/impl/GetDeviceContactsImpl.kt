package me.bookk.feature.clients.domain.impl

import me.bookk.feature.clients.domain.api.GetDeviceContacts
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.datasource.DeviceContact
import me.bookk.feature.clients.domain.datasource.DeviceContactsDataSource

internal class GetDeviceContactsImpl(
    private val deviceContactsDataSource: DeviceContactsDataSource
) : GetDeviceContacts {

    override suspend fun invoke(): List<ClientImportEntry> {
        return deviceContactsDataSource.getContacts()
            .map { it.toImportEntry() }
            .filter { it.phone != null || it.email != null }
    }

    private fun DeviceContact.toImportEntry(): ClientImportEntry {
        return ClientImportEntry(
            name = name,
            lastName = lastName,
            phone = phones.firstNotNullOfOrNull { it.toDialablePhone() },
            email = emails.firstOrNull { it.isNotBlank() }
        )
    }

    private fun String.toDialablePhone(): String? {
        val number = takeWhile { it !in NUMBER_END_MARKERS && !it.isLetter() }
        val digits = number.mapNotNull { it.digitToIntOrNull() }.joinToString(separator = "")
        if (digits.isEmpty()) return null
        val isInternational = number.firstOrNull { it == '+' || it.isDigit() } == '+'
        return if (isInternational) "+$digits" else digits
    }

    private companion object {
        val NUMBER_END_MARKERS = setOf(',', ';', '#', '*')
    }
}

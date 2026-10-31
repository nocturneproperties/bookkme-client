package me.bookk.feature.clients.domain.impl

import me.bookk.feature.clients.domain.api.GetDeviceContacts
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.datasource.DeviceContactsDataSource

internal class GetDeviceContactsImpl(
    private val deviceContactsDataSource: DeviceContactsDataSource
) : GetDeviceContacts {

    override suspend fun invoke(): List<ClientImportEntry> {
        return deviceContactsDataSource.getContacts()
    }
}

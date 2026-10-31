package me.bookk.feature.clients.domain.datasource

import me.bookk.feature.clients.domain.api.entity.ClientImportEntry

interface DeviceContactsDataSource {
    suspend fun getContacts(): List<ClientImportEntry>
}

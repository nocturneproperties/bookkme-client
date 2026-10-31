package me.bookk.feature.clients.domain.datasource

interface DeviceContactsDataSource {
    suspend fun getContacts(): List<DeviceContact>
}

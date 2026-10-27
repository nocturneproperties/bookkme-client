package me.bookk.feature.authorization.domain.datasource.device

interface DeviceDataSource {
    suspend fun getOrCreateDeviceUUID(): String
}
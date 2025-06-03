package me.bookk.feature.authorization.data.datasource

import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class CommonDeviceDataSource(
    preferenceProvider: PreferenceProvider
) : DeviceDataSource {

    private val preferences = preferenceProvider.get("device_prefs")

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getOrCreateDeviceUUID(): String {
        var uuid = preferences.get(Key.deviceUUID)
        if (uuid == null) {
            uuid = Uuid.random().toString()
            preferences.set(Key.deviceUUID, uuid)
        }
        return uuid
    }

    private object Key {
        val deviceUUID = Preferences.Key<String>("device_uuid")
    }
}
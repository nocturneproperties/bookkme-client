package me.bookk.feature.authorization.data.datasource

import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.platform.domain.datasource.PreferenceProvider
import me.bookk.feature.platform.domain.datasource.Preferences
import me.bookk.feature.platform.domain.datasource.get
import me.bookk.feature.platform.domain.datasource.set
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
            uuid = Uuid.random().toHexString()
            preferences.set(Key.deviceUUID, uuid)
        }
        return uuid
    }

    private object Key {
        val deviceUUID = Preferences.Key<String>("device_uuid")
    }
}
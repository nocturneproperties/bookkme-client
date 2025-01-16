package me.bookk.feature.authorization.data.datasource

import me.bookk.core.Platform
import me.bookk.core.storage.PreferenceProvider
import me.bookk.core.storage.Preferences
import me.bookk.core.storage.get
import me.bookk.core.storage.set
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
            uuid = Uuid.random().toHexString()
            preferences.set(Key.deviceUUID, uuid)
        }
        return uuid
    }

    override suspend fun getDeviceName(): String {
        return Platform().getDeviceName()
    }

    private object Key {
        val deviceUUID = Preferences.Key<String>("device_uuid")
    }
}
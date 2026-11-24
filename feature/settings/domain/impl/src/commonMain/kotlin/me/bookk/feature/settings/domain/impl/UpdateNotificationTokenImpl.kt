package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.fetch.BackgroundDataFetchAction
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.settings.domain.api.UpdateNotificationToken
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource

internal class UpdateNotificationTokenImpl(
    private val notificationSettingsDataSource: NotificationSettingsDataSource,
    private val deviceDataSource: DeviceDataSource,
) : UpdateNotificationToken,
    BackgroundDataFetchAction {

    override suspend fun invoke(token: String) {
        notificationSettingsDataSource.saveNotificationToken(token)
        upload(token)
    }

    override suspend fun invoke() {
        notificationSettingsDataSource.getNotificationToken()?.let { upload(it) }
    }

    override suspend fun doOnBackgroundDataFetch() {
        invoke()
    }

    private suspend fun upload(token: String) {
        val deviceUuid = deviceDataSource.getOrCreateDeviceUUID()
        notificationSettingsDataSource.updateNotificationToken(deviceUuid, token)
    }
}

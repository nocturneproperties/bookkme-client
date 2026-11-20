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
        val deviceUuid = deviceDataSource.getOrCreateDeviceUUID()
        runCatching {
            notificationSettingsDataSource.updateNotificationToken(deviceUuid, token)
        }.onSuccess {
            notificationSettingsDataSource.savePendingNotificationToken(null)
        }.onFailure {
            notificationSettingsDataSource.savePendingNotificationToken(token)
        }.getOrThrow()
    }

    override suspend fun invoke() {
        notificationSettingsDataSource.getPendingNotificationToken()?.let {
            invoke(it)
        }
    }

    override suspend fun doOnBackgroundDataFetch() {
        invoke()
    }
}

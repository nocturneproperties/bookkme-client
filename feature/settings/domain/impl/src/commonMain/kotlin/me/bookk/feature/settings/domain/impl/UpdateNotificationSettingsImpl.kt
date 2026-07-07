package me.bookk.feature.settings.domain.impl

import me.bookk.feature.settings.domain.api.UpdateNotificationSettings
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource

internal class UpdateNotificationSettingsImpl(
    private val notificationSettingsDataSource: NotificationSettingsDataSource
) : UpdateNotificationSettings {
    override suspend fun invoke(settings: NotificationSettings): NotificationSettings {
        val result = notificationSettingsDataSource.updateNotificationSettings(settings)
        notificationSettingsDataSource.saveNotificationSettingsInDB(result)
        return result
    }
}

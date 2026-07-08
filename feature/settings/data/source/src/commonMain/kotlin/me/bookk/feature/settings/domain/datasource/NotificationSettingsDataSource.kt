package me.bookk.feature.settings.domain.datasource

import me.bookk.feature.settings.domain.api.entity.Device
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import kotlin.uuid.Uuid

interface NotificationSettingsDataSource {
    suspend fun getNotificationSettings(): NotificationSettings

    suspend fun updateNotificationSettings(settings: NotificationSettings): NotificationSettings

    suspend fun getNotificationSettingsFromDB(userId: Uuid): NotificationSettings?

    suspend fun saveNotificationSettingsInDB(settings: NotificationSettings)

    suspend fun updateNotificationToken(deviceUuid: String, token: String): Device

    suspend fun savePendingNotificationToken(token: String?)
    suspend fun getPendingNotificationToken(): String?
}

package me.bookk.feature.settings.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.settings.domain.api.entity.Device
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import kotlin.uuid.Uuid

interface NotificationSettingsDataSource {
    suspend fun getNotificationSettings(): NotificationSettings

    suspend fun updateNotificationSettings(settings: NotificationSettings): NotificationSettings

    fun observeNotificationSettingsDBChanges(userId: Uuid): Flow<NotificationSettings?>

    suspend fun saveNotificationSettingsInDB(settings: NotificationSettings)

    suspend fun updateNotificationToken(deviceUuid: String, token: String): Device

    suspend fun savePendingNotificationToken(token: String?)
    suspend fun getPendingNotificationToken(): String?
}

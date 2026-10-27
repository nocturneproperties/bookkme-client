package me.bookk.feature.settings.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.settings.domain.api.entity.NotificationSettings

interface GetNotificationSettings {
    fun flow(): Flow<NotificationSettings?>
    suspend fun refresh(): NotificationSettings
}

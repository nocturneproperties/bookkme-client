package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.NotificationSettings

interface GetNotificationSettings {
    suspend operator fun invoke(): NotificationSettings
    suspend fun cached(onResultAvailable: suspend (NotificationSettings) -> Unit)
}

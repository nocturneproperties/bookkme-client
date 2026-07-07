package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.NotificationSettings

interface UpdateNotificationSettings {
    suspend operator fun invoke(settings: NotificationSettings): NotificationSettings
}

package me.bookk.feature.settings.domain.impl

import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource

internal class GetNotificationSettingsImpl(
    private val notificationSettingsDataSource: NotificationSettingsDataSource,
    private val userProfileCRUD: UserProfileCRUD
) : GetNotificationSettings {
    override suspend fun invoke(): NotificationSettings {
        val userId = userProfileCRUD.get().id
        return notificationSettingsDataSource.getNotificationSettingsFromDB(userId)
            ?: notificationSettingsDataSource.getNotificationSettings()
                .also { notificationSettingsDataSource.saveNotificationSettingsInDB(it) }
    }

    override suspend fun cached(onResultAvailable: suspend (NotificationSettings) -> Unit) {
        val userId = userProfileCRUD.get().id
        notificationSettingsDataSource.getNotificationSettingsFromDB(userId)?.let {
            onResultAvailable(it)
        }
        onResultAvailable(
            notificationSettingsDataSource.getNotificationSettings()
                .also { notificationSettingsDataSource.saveNotificationSettingsInDB(it) }
        )
    }
}

package me.bookk.feature.settings.domain.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource

internal class GetNotificationSettingsImpl(
    private val notificationSettingsDataSource: NotificationSettingsDataSource,
    private val userProfileCRUD: UserProfileCRUD
) : GetNotificationSettings {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun flow(): Flow<NotificationSettings?> {
        return userProfileCRUD.observe()
            .flatMapLatestOrNull {
                notificationSettingsDataSource.observeNotificationSettingsDBChanges(it.id)
            }
    }

    override suspend fun refresh(): NotificationSettings {
        userProfileCRUD.get()
        return notificationSettingsDataSource.getNotificationSettings()
            .also { notificationSettingsDataSource.saveNotificationSettingsInDB(it) }
    }
}

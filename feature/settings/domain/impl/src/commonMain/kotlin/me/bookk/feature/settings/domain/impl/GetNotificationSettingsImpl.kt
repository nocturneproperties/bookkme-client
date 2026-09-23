package me.bookk.feature.settings.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource

internal class GetNotificationSettingsImpl(
    private val notificationSettingsDataSource: NotificationSettingsDataSource,
    private val userProfileCRUD: UserProfileCRUD
) : GetNotificationSettings {

    override fun flow(): Flow<NotificationSettings?> = flow {
        val userId = userProfileCRUD.get().id
        emitAll(notificationSettingsDataSource.observeNotificationSettingsDBChanges(userId))
    }

    override suspend fun refresh(): NotificationSettings =
        notificationSettingsDataSource.getNotificationSettings()
            .also { notificationSettingsDataSource.saveNotificationSettingsInDB(it) }
}

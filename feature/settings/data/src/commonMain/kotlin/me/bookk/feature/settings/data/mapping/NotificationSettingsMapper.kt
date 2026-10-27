package me.bookk.feature.settings.data.mapping

import me.bookk.database.entity.NotificationSettingsChannelEntity
import me.bookk.database.entity.NotificationSettingsEntity
import me.bookk.database.relation.NotificationSettingsLocal
import me.bookk.feature.settings.data.remote.model.NotificationChannelRemote
import me.bookk.feature.settings.data.remote.model.NotificationChannelSettingsRemote
import me.bookk.feature.settings.data.remote.model.UpdateNotificationSettingsRequestRemote
import me.bookk.feature.settings.domain.api.entity.NotificationChannel
import me.bookk.feature.settings.domain.api.entity.NotificationChannelSettings
import me.bookk.feature.settings.domain.api.entity.NotificationSettings

internal fun NotificationSettings.toUpdateRequestRemote() = UpdateNotificationSettingsRequestRemote(
    id = id,
    appointmentEnabled = appointmentEnabled,
    channels = channels.map { it.toRemote() }
)

private fun NotificationChannelSettings.toRemote() = NotificationChannelSettingsRemote(
    id = id,
    channel = channel.toRemote(),
    enabled = enabled,
    availableToClients = availableToClients
)

private fun NotificationChannel.toRemote() = when (this) {
    NotificationChannel.TELEGRAM -> NotificationChannelRemote.TELEGRAM
    NotificationChannel.EMAIL -> NotificationChannelRemote.EMAIL
    NotificationChannel.PUSH_NOTIFICATIONS -> NotificationChannelRemote.PUSH_NOTIFICATIONS
}

internal fun NotificationSettings.toEntity() = NotificationSettingsEntity(
    id = id,
    userId = userId,
    appointmentEnabled = appointmentEnabled
)

internal fun NotificationSettings.toChannelEntities() = channels.map { channel ->
    NotificationSettingsChannelEntity(
        id = channel.id,
        settingsId = id,
        channel = channel.channel.name,
        enabled = channel.enabled,
        availableToClients = channel.availableToClients
    )
}

internal fun NotificationSettingsLocal.toDomain() = NotificationSettings(
    id = entity.id,
    userId = entity.userId,
    appointmentEnabled = entity.appointmentEnabled,
    channels = channels.map {
        NotificationChannelSettings(
            id = it.id,
            channel = NotificationChannel.valueOf(it.channel),
            enabled = it.enabled,
            availableToClients = it.availableToClients
        )
    }
)

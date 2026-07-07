package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.settings.domain.api.entity.NotificationChannel
import me.bookk.feature.settings.domain.api.entity.NotificationChannelSettings
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import kotlin.uuid.Uuid

@Serializable
data class NotificationSettingsRemote(
    val id: Uuid,
    val userId: Uuid,
    val appointmentEnabled: Boolean,
    val channels: List<NotificationChannelSettingsRemote>
) {
    fun toDomain() = NotificationSettings(
        id = id,
        userId = userId,
        appointmentEnabled = appointmentEnabled,
        channels = channels.map { it.toDomain() }
    )
}

@Serializable
data class NotificationChannelSettingsRemote(
    val id: Uuid,
    val channel: NotificationChannelRemote,
    val enabled: Boolean
) {
    fun toDomain() = NotificationChannelSettings(
        id = id,
        channel = channel.toDomain(),
        enabled = enabled
    )
}

@Serializable
enum class NotificationChannelRemote {
    TELEGRAM,
    EMAIL,
    PUSH_NOTIFICATIONS;

    fun toDomain() = when (this) {
        TELEGRAM -> NotificationChannel.TELEGRAM
        EMAIL -> NotificationChannel.EMAIL
        PUSH_NOTIFICATIONS -> NotificationChannel.PUSH_NOTIFICATIONS
    }
}

@Serializable
data class UpdateNotificationSettingsRequestRemote(
    val id: Uuid,
    val appointmentEnabled: Boolean,
    val channels: List<NotificationChannelSettingsRemote>
)

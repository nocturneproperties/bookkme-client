package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.settings.domain.api.entity.NotificationChannel
import me.bookk.feature.settings.domain.api.entity.NotificationChannelSettings
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import kotlin.uuid.Uuid

@Serializable
data class NotificationSettingsRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val userId: Uuid,
    @ProtoNumber(3) val appointmentEnabled: Boolean,
    @ProtoNumber(4) val channels: List<NotificationChannelSettingsRemote>
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
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val channel: NotificationChannelRemote,
    @ProtoNumber(3) val enabled: Boolean,
    @ProtoNumber(4) val availableToClients: Boolean
) {
    fun toDomain() = NotificationChannelSettings(
        id = id,
        channel = channel.toDomain(),
        enabled = enabled,
        availableToClients = availableToClients
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
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val appointmentEnabled: Boolean,
    @ProtoNumber(3) val channels: List<NotificationChannelSettingsRemote>
)

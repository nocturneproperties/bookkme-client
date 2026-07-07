package me.bookk.feature.settings.domain.api.entity

import kotlin.uuid.Uuid

data class NotificationSettings(
    val id: Uuid,
    val userId: Uuid,
    val appointmentEnabled: Boolean,
    val channels: List<NotificationChannelSettings>
) {
    companion object {
        fun stub(userId: Uuid = Uuid.random()) = NotificationSettings(
            id = Uuid.random(),
            userId = userId,
            appointmentEnabled = true,
            channels = NotificationChannel.entries.map {
                NotificationChannelSettings(id = Uuid.random(), channel = it, enabled = true)
            }
        )
    }
}

data class NotificationChannelSettings(
    val id: Uuid,
    val channel: NotificationChannel,
    val enabled: Boolean
)

enum class NotificationChannel {
    TELEGRAM,
    EMAIL,
    PUSH_NOTIFICATIONS
}

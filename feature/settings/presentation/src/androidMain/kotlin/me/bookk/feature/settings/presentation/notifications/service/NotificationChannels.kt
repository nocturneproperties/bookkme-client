package me.bookk.feature.settings.presentation.notifications.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import me.bookk.android.feature.settings.resources.SettingsRes

object NotificationChannels {
    fun createDefaultChannel(context: Context) {
        val defaultChannelId = SettingsRes.strings.settings_notifications_channel_id.getString(context)
        val name = SettingsRes.strings.settings_notifications_channel_title.getString(context)
        val descr = SettingsRes.strings.settings_notifications_channel_description.getString(context)
        val channel = NotificationChannel(
            defaultChannelId,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = descr
            enableVibration(true)
        }

        context
            .getSystemService<NotificationManager>()
            ?.createNotificationChannel(channel)
    }
}
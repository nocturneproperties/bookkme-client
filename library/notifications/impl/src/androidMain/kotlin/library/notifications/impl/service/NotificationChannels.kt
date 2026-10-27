package library.notifications.impl.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import library.notifications.resources.NotifcationRes

internal object NotificationChannels {
    fun createDefaultChannel(context: Context) {
        val defaultChannelId = NotifcationRes.strings.settings_notifications_channel_id.getString(context)
        val name = NotifcationRes.strings.settings_notifications_channel_title.getString(context)
        val descr = NotifcationRes.strings.settings_notifications_channel_description.getString(context)
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
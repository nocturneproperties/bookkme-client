package library.notifications.impl

import android.app.PendingIntent
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import library.notifications.api.LocalNotification
import library.notifications.api.LocalNotificationManager
import library.notifications.resources.NotifcationRes
import me.bookk.core.Logger


class AndroidLocalNotificationManager(
    private val appContext: Context
) : LocalNotificationManager {

    private val logger = Logger.create("LocalNotificationManager")
    private val notificationManager = NotificationManagerCompat.from(appContext)


    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    override fun show(notification: LocalNotification) {
        val defaultChannelId = NotifcationRes.strings.settings_notifications_channel_id.getString(appContext)
        val builder = NotificationCompat.Builder(appContext, defaultChannelId)
            .setSmallIcon(appContext.applicationInfo.icon)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setContentIntent(launchAppPendingIntent(notification))

        notification.group?.let { builder.setGroup(it) }
        notification.subtitle?.let { builder.setSubText(it) }
        notification.badgeCount?.let { builder.setNumber(it) }

        runCatching {
            notificationManager.notify(notification.id.hashCode(), builder.build())
        }.onFailure {
            logger.e(it)
        }
    }

    override fun cancel(id: String) {
        notificationManager.cancel(id.hashCode())
    }

    override fun cancelAll() {
        notificationManager.cancelAll()
    }

    private fun launchAppPendingIntent(notification: LocalNotification): PendingIntent? {
        val launchIntent = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)
            ?: return null
        notification.data.forEach { (key, value) -> launchIntent.putExtra(key, value) }
        return PendingIntent.getActivity(
            appContext,
            notification.id.hashCode(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

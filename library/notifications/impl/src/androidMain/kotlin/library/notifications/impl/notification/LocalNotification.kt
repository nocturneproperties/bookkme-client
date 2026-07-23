package library.notifications.impl.notification

import android.content.res.Resources
import com.google.firebase.messaging.RemoteMessage
import library.notifications.api.LocalNotification
import kotlin.uuid.Uuid

internal fun RemoteMessage.asLocalNotification(resources: Resources): LocalNotification? {
    val notification = notification ?: return null
    val titleKey = notification.titleLocalizationKey ?: return null
    val titleArgs = notification.titleLocalizationArgs ?: return null

    val bodyKey = notification.bodyLocalizationKey ?: return null
    val bodyArgs = notification.bodyLocalizationArgs ?: return null

    val title = resources.resolveResource(titleKey, titleArgs) ?: return null
    val body = resources.resolveResource(bodyKey, bodyArgs) ?: return null

    return LocalNotification(
        id = messageId ?: Uuid.random().toString(),
        title = title,
        body = body,
        data = data
    )
}

private fun Resources.resolveResource(key: String, args: Array<String>): String? {
    return when(key) {
        else -> null
    }
}
package library.notifications.impl.service

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import library.notifications.api.NotificationBridge
import library.notifications.api.TokenBridge
import library.notifications.impl.notification.asLocalNotification

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FCMService : FirebaseMessagingService() {

    override fun onRegistered(installationId: String) {
        super.onRegistered(installationId)
        TokenBridge.updateInstallationId(installationId)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        NotificationChannels.createDefaultChannel(this)
        message.asLocalNotification(resources)?.let { NotificationBridge.showNotification(it) }
    }
}
package me.bookk.feature.settings.presentation.notifications.service

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import me.bookk.feature.settings.presentation.notifications.token.TokenBridge

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FCMService : FirebaseMessagingService() {

    override fun onRegistered(installationId: String) {
        super.onRegistered(installationId)
        TokenBridge.updateInstallationId(installationId)
    }
}
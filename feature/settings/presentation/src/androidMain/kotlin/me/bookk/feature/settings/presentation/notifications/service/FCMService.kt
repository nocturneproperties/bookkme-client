package me.bookk.feature.settings.presentation.notifications.service

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.bookk.core.Logger
import me.bookk.feature.settings.domain.api.UpdateNotificationToken
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FCMService : FirebaseMessagingService(), KoinComponent {

    private val updateNotificationToken: UpdateNotificationToken by inject()
    private val logger = Logger.create("FCMService")
    private val applicationScope: CoroutineScope by inject()

    override fun onRegistered(installationId: String) {
        super.onRegistered(installationId)
        applicationScope.launch {
            runCatching {
                updateNotificationToken(installationId)
            }.onFailure {
                logger.e("InstallationID push failed, postponing.", it)
            }.onSuccess {
                logger.i("InstallationID push success.")
            }
        }
    }
}
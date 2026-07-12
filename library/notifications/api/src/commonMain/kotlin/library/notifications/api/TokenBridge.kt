package library.notifications.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.bookk.core.Logger
import me.bookk.feature.settings.domain.api.UpdateNotificationToken
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TokenBridge : KoinComponent {

    private val applicationScope: CoroutineScope by inject()
    private val updateNotificationToken: UpdateNotificationToken by inject()
    private val logger = Logger.create("TokenBridge")

    fun updateInstallationId(token: String) {
        applicationScope.launch {
            runCatching {
                updateNotificationToken(token)
            }.onFailure {
                logger.e("InstallationID push failed, postponing.", it)
            }.onSuccess {
                logger.i("InstallationID push success.")
            }
        }
    }
}
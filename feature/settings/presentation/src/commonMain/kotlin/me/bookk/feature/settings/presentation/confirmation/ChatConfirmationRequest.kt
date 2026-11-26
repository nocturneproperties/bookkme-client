package me.bookk.feature.settings.presentation.confirmation

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.settings.domain.api.entity.ActionOutcome
import me.bookk.feature.settings.domain.api.entity.ChatConfirmationError
import kotlin.time.Duration.Companion.seconds

internal class ChatConfirmationRequest<T>(
    private val viewModel: ViewModel,
    private val key: String,
    private val state: ChatConfirmationState,
    private val button: ButtonState,
    private val notifications: PresentationNotificationState,
    private val action: suspend () -> ActionOutcome<T>,
    private val onCompleted: suspend (T) -> Unit,
    private val onError: suspend (Throwable) -> Unit,
    private val isActionAllowed: () -> Boolean = { true },
) {
    var isCoolingDown = false
        private set

    fun canSend(): Boolean {
        return !isCoolingDown && isActionAllowed()
    }

    fun send() {
        send(isRetry = false)
    }

    fun onWriteAccessAnswered(isGranted: Boolean) {
        state.isWriteAccessRequested = false
        if (isGranted) send(isRetry = true) else showWriteAccessRefused()
    }

    private fun send(isRetry: Boolean) {
        viewModel.launch(
            key = key,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { button.startLoading() },
            call = { action() },
            onComplete = { onOutcome(it) },
            onError = { onSendFailed(it, isRetry) },
            onTerminate = { button.stopLoading(enable = canSend()) },
        )
    }

    private suspend fun onOutcome(outcome: ActionOutcome<T>) {
        when (outcome) {
            is ActionOutcome.Completed -> onCompleted(outcome.value)
            ActionOutcome.ConfirmationSent -> showSent()
        }
    }

    private suspend fun onSendFailed(error: Throwable, isRetry: Boolean) {
        when (error) {
            is ChatConfirmationError.TooFrequent -> showSent()
            is ChatConfirmationError.WriteAccessRequired -> onWriteAccessRequired(isRetry)
            else -> onError(error)
        }
    }

    private fun onWriteAccessRequired(isRetry: Boolean) {
        if (isRetry) {
            showWriteAccessRefused()
        } else {
            state.isWriteAccessRequested = true
        }
    }

    private fun showSent() {
        state.notice = SettingsRes.strings.settings_chat_confirmation_sent.desc()
        button.text = SettingsRes.strings.settings_chat_confirmation_send_again.desc()
        startCooldown()
    }

    private fun startCooldown() {
        isCoolingDown = true
        viewModel.launch(
            key = "$key$COOLDOWN_KEY_SUFFIX",
            launchIn = DispatcherProvider.default,
            call = { delay(COOLDOWN) },
            onComplete = {
                isCoolingDown = false
                button.isEnabled = canSend()
            },
            onError = {},
        )
    }

    private fun showWriteAccessRefused() {
        notifications.add(
            PresentationNotification.Message.simple(
                SettingsRes.strings.settings_chat_confirmation_write_access_refused.desc(),
            ),
        )
    }

    private companion object {
        const val COOLDOWN_KEY_SUFFIX = "ChatConfirmationCooldown"
        val COOLDOWN = 60.seconds
    }
}

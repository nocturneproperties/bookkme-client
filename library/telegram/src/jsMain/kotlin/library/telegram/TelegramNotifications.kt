package library.telegram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.site.designsystem.resources.localized

@Composable
fun ObserveTelegramNotifications(webApp: TelegramWebApp, state: PresentationNotificationState) {
    var presentedCount by remember(state) { mutableIntStateOf(0) }
    val notification = state.presentationNotification.firstOrNull() ?: return
    val popup = notification.telegramPopup()
    LaunchedEffect(notification, presentedCount) {
        webApp.present(notification, popup)
        state.removeFirst()
        presentedCount++
    }
}

@Composable
private fun PresentationNotification.telegramPopup(): TelegramPopup? {
    return when (this) {
        is PresentationNotification.Message -> TelegramPopup.create(
            title = title?.localized(),
            message = message.localized(),
            buttons = buttons.map { TelegramPopupButton.of(it, it.text.localized()) },
        )

        is PresentationNotification.GlobalMessage -> TelegramPopup.globalMessage(
            text = text.localized(),
            actionText = actionText?.localized(),
            onAction = onActionClick,
        )

        else -> null
    }
}

private suspend fun TelegramWebApp.present(
    notification: PresentationNotification,
    popup: TelegramPopup?,
) {
    when (notification) {
        is PresentationNotification.InputMessage -> notification.onCancel()

        PresentationNotification.SuccessHaptic -> hapticFeedback.notificationOccurred(
            TelegramHaptics.SUCCESS,
        )

        else -> popup?.let { show(it) }
    }
}

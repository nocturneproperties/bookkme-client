package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.localized
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState

@Composable
fun ObserveNotifications(state: PresentationNotificationState) {
    state.presentationNotification.forEach { notification ->
        when (notification) {
            is PresentationNotification.Message -> {
                AppDialog(
                    title = notification.title?.localized(),
                    subtitle = notification.message.localized(),
                    rightButton = AndroidButtonState(notification.buttonText),
                    onRightButtonClicked = { state.removeFirst() },
                    onDismiss = { state.removeFirst() },
                )
            }

            PresentationNotification.Ignore -> {
                state.removeFirst()
                return
            }
        }
    }
}
package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.LocalUnauthorizedHandler
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.PresentationNotificationState

@Composable
fun ObserveNotifications(state: PresentationNotificationState) {
    state.presentationNotification.firstOrNull()?.let { notification ->
        when (notification) {
            is PresentationNotification.Message -> {
                AppDialog(
                    title = notification.title?.localized(),
                    subtitle = notification.message.localized(),
                    buttonDescriptors = notification.buttons,
                    onDismiss = { state.removeFirst() },
                )
            }

            is PresentationNotification.InputMessage -> {
                val fieldState = remember(notification) {
                    AndroidTextFieldState(
                        text = notification.initialText,
                        placeholder = notification.placeholder ?: "".desc()
                    ).apply {
                        onTextChanged = { text = it }
                    }
                }
                val descriptors = remember(notification) {
                    listOf(
                        ButtonDescriptor(
                            text = notification.cancelText,
                            onClick = notification.onCancel
                        ),
                        ButtonDescriptor(
                            text = notification.confirmText,
                            actionType = notification.confirmActionType,
                            onClick = { notification.onConfirm(fieldState.text) }
                        )
                    )
                }
                AppDialog(
                    title = notification.title.localized(),
                    subtitle = notification.message.localized(),
                    content = { TextField(state = fieldState) },
                    buttonDescriptors = descriptors,
                    onDismiss = { state.removeFirst() },
                )
            }

            is PresentationNotification.GlobalMessage -> {
                val text = notification.text.localized()
                val label = notification.actionText?.localized()
                val provider = LocalSnackbarProvider.current
                LaunchedEffect(notification.id) {
                    provider.showSnackbar(
                        text = text,
                        actionLabel = label,
                        state = notification.state,
                        duration = notification.duration,
                        onActionClick = notification.onActionClick
                    )
                    state.removeFirst()
                }
                return
            }
            PresentationNotification.Unauthorized -> {
                LocalUnauthorizedHandler.current.onUnauthorized()
                state.removeFirst()
                return
            }

            PresentationNotification.Ignore -> {
                state.removeFirst()
                return
            }
        }
    }
}
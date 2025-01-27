package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.localized
import me.bookk.core.presentation.error.PresentationError
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.ErrorState

@Composable
fun ObserveErrors(state: ErrorState) {
    state.presentationError.forEach { error ->
        ErrorDialog(
            error = error,
            onDismiss = { state.removeFirst() }
        )
    }
}

@Composable
fun ErrorDialog(error: PresentationError, onDismiss: () -> Unit) {
    when (error) {
        is PresentationError.Message -> {
            AppDialog(
                title = error.title?.localized(),
                subtitle = error.message.localized(),
                rightButton = AndroidButtonState(error.buttonText),
                onRightButtonClicked = { onDismiss() },
                onDismiss = { onDismiss() },
            )
        }

        PresentationError.Ignore -> {
            onDismiss()
            return
        }
    }
}
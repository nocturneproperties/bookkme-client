package me.bookk.designsystem.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage


interface SnackbarProvider {
    fun showSnackbar(
        text: String,
        actionLabel: String?,
        state: GlobalMessage.State,
        duration: GlobalMessage.Duration,
        onActionClick: (() -> Unit)?
    )
}

class StatefulSnackbarVisuals(
    override val message: String,
    val state: GlobalMessage.State,
    duration: GlobalMessage.Duration,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
) : SnackbarVisuals {
    override val duration: SnackbarDuration = when (duration) {
        GlobalMessage.Duration.SHORT -> SnackbarDuration.Short
        GlobalMessage.Duration.LONG -> SnackbarDuration.Long
        GlobalMessage.Duration.INDEFINITE -> SnackbarDuration.Indefinite
    }
}

class DefaultSnackbarProvider(
    private val scope: CoroutineScope,
    private val snackbarHostState: SnackbarHostState
) : SnackbarProvider {
    override fun showSnackbar(
        text: String,
        actionLabel: String?,
        state: GlobalMessage.State,
        duration: GlobalMessage.Duration,
        onActionClick: (() -> Unit)?
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                StatefulSnackbarVisuals(
                    message = text,
                    state = state,
                    duration = duration,
                    actionLabel = actionLabel
                )
            )
            when (result) {
                SnackbarResult.Dismissed -> {}
                SnackbarResult.ActionPerformed -> {
                    onActionClick?.invoke()
                }
            }
        }
    }
}

val LocalSnackbarProvider = staticCompositionLocalOf<SnackbarProvider> {
    object : SnackbarProvider {
        override fun showSnackbar(
            text: String,
            actionLabel: String?,
            state: GlobalMessage.State,
            duration: GlobalMessage.Duration,
            onActionClick: (() -> Unit)?
        ) {
        }
    }
}
package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.Flow
import me.bookk.core.presentation.error.PresentationError
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AndroidButtonState

@Composable
fun ObserveErrors(errorFlow: Flow<PresentationError>) {
    val errors = remember { mutableStateListOf<PresentationError>() }

    LaunchedEffect(errorFlow) {
        errorFlow.collect { error ->
             errors.add(error)
        }
    }

    errors.forEach { error ->
        ErrorDialog(
            error = error,
            onDismiss = { errors.remove(error) }
        )
    }
}

@Composable
fun ErrorDialog(error: PresentationError, onDismiss: () -> Unit) {
    AppDialog(
        subtitle = when (error) {
            is PresentationError.Message -> error.message
            PresentationError.NoConnection -> DesignSystem.strings.error_no_internet.desc()
            PresentationError.ServerError -> DesignSystem.strings.error_server.desc()
            is PresentationError.Unsupported -> DesignSystem.strings.error_unexpected.desc()
            PresentationError.Ignore -> {
                onDismiss()
                return
            }
        }.localized(),
        rightButton = AndroidButtonState(DesignSystem.strings.action_ok.desc()),
        onRightButtonClicked = { onDismiss() },
        onDismiss = { onDismiss() },
    )
}
package me.bookk.designsystem.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.Flow
import me.bookk.core.presentation.error.PresentationError
import me.bookk.core.presentation.showToast
import me.bookk.designsystem.uistate.ButtonStateImpl

@Composable
fun ObserveErrors(errorFlow: Flow<PresentationError>) {
    val context = LocalContext.current
    val errors = remember { mutableStateListOf<PresentationError>() }

    LaunchedEffect(errorFlow) {
        errorFlow.collect { error ->
            when (error) {
                is PresentationError.Unsupported -> {
                    context.showToast(
                        message = error.message?.desc() ?: "Unknown error".desc(),
                        length = Toast.LENGTH_LONG
                    )
                }

                else -> errors.add(error)
            }
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
            else -> "Not yet implemented".desc()
        }.localized(),
        rightButton = ButtonStateImpl("OK".desc()),
        onRightButtonClicked = { onDismiss() },
        onDismiss = { onDismiss() },
    )
}
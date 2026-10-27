package me.bookk.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalViewConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun longPressInteractionSource(onLongPress: () -> Unit): MutableInteractionSource {
    val longPressTimeoutMillis = LocalViewConfiguration.current.longPressTimeoutMillis
    return remember { MutableInteractionSource() }
        .also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collectLatest { interaction ->
                    if (interaction is PressInteraction.Press) {
                        delay(longPressTimeoutMillis.milliseconds)
                        onLongPress()
                    }
                }
            }
        }
}

package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
internal fun ObserveWindowResize(onResize: () -> Unit) {
    val currentOnResize by rememberUpdatedState(onResize)
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = { currentOnResize() }
        window.addEventListener("resize", listener)
        onDispose { window.removeEventListener("resize", listener) }
    }
}

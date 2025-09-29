package me.bookk.designsystem.action

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun HideFromIme(content: @Composable () -> Unit) {
    val isKeyboardOpen by keyboardAsState()
    AnimatedVisibility(
        visible = !isKeyboardOpen,
        enter = expandVertically(animationSpec = spring()),
        exit = shrinkVertically(animationSpec = spring()),
    ) {
        content()
    }
}
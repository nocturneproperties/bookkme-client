package me.bookk.designsystem.action

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun keyboardMovingDirection(): State<Int> {
    val state = remember { mutableIntStateOf(0) }
    val lastImeBottom = remember { mutableIntStateOf(0) }
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val isImeVisible = WindowInsets.isImeVisible
    LaunchedEffect(imeBottom, isImeVisible) {
        state.intValue = when {
            isImeVisible && lastImeBottom.intValue < imeBottom -> 1
            isImeVisible && lastImeBottom.intValue > imeBottom -> -1
            else -> 0
        }
        lastImeBottom.intValue = imeBottom
    }
    return state
}

fun isKeyboardMovingDownOrInvisible(state: Int): Boolean {
    return state != 1
}

@Composable
fun HideFromIme(content: @Composable () -> Unit) {
    val direction by keyboardMovingDirection()
    AnimatedVisibility(
        visible = isKeyboardMovingDownOrInvisible(direction).also { Log.d("ADD", it.toString()) },
        enter = expandVertically(animationSpec = spring()),
        exit = shrinkVertically(animationSpec = spring()),
    ) {
        content()
    }
}
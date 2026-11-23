package me.bookk.core.presentation.navigation

import androidx.compose.runtime.compositionLocalOf
import me.bookk.core.presentation.ViewModelStore

class BackStackEntry<R : Any>(
    val route: R,
) {
    val viewModelStore = ViewModelStore()
    var scrollTop: Double = 0.0
}

val LocalIsCurrentScreen = compositionLocalOf { true }

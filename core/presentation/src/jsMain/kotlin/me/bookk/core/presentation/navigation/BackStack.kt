package me.bookk.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import me.bookk.core.presentation.LocalViewModelStore
import me.bookk.core.presentation.ViewModelStore

class BackStack<R : Any>(
    initial: R,
) {
    private val entries = mutableStateListOf(BackStackEntry(initial))

    val currentEntry: BackStackEntry<R>
        get() = entries.last()

    val current: R
        get() = currentEntry.route

    val canGoBack: Boolean
        get() = entries.size > 1

    fun push(route: R) {
        entries.add(BackStackEntry(route))
    }

    fun pop() {
        if (!canGoBack) return
        entries.removeAt(entries.lastIndex).viewModelStore.clear()
    }

    fun clear() {
        entries.forEach { it.viewModelStore.clear() }
    }
}

class BackStackEntry<R : Any>(
    val route: R,
) {
    val viewModelStore = ViewModelStore()
}

@Composable
fun <R : Any> rememberBackStack(initial: R): BackStack<R> {
    val stack = remember { BackStack(initial) }
    DisposableEffect(stack) {
        onDispose { stack.clear() }
    }
    return stack
}

@Composable
fun <R : Any> BackStackHost(stack: BackStack<R>, content: @Composable (R) -> Unit) {
    val entry = stack.currentEntry
    key(entry) {
        CompositionLocalProvider(LocalViewModelStore provides entry.viewModelStore) {
            content(entry.route)
        }
    }
}

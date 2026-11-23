package me.bookk.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class BackStack<R : Any>(
    initial: R,
) {
    private val entries = mutableStateListOf(BackStackEntry(initial))

    var transition: BackStackTransition<R>? by mutableStateOf(null)
        private set

    val currentEntry: BackStackEntry<R>
        get() = entries.last()

    val current: R
        get() = currentEntry.route

    val canGoBack: Boolean
        get() = entries.size > 1

    val visibleEntries: List<BackStackEntry<R>>
        get() = transition?.entriesInStackOrder ?: listOf(currentEntry)

    fun push(route: R) {
        finishTransition()
        val from = currentEntry
        entries.add(BackStackEntry(route))
        transition = BackStackTransition(from, currentEntry, BackStackDirection.FORWARD)
    }

    fun pop() {
        if (!canGoBack) return
        finishTransition()
        val leaving = entries.removeAt(entries.lastIndex)
        transition = BackStackTransition(leaving, currentEntry, BackStackDirection.BACKWARD)
    }

    fun finishTransition() {
        val finished = transition ?: return
        transition = null
        if (finished.direction == BackStackDirection.BACKWARD) {
            finished.from.viewModelStore.clear()
        }
    }

    fun clear() {
        finishTransition()
        entries.forEach { it.viewModelStore.clear() }
    }
}

@Composable
fun <R : Any> rememberBackStack(initial: R): BackStack<R> {
    val stack = remember { BackStack(initial) }
    DisposableEffect(stack) {
        onDispose { stack.clear() }
    }
    return stack
}

package me.bookk.core.presentation.memory

import me.bookk.core.LogFactory
import me.bookk.core.presentation.ViewModel

val closureLogger = LogFactory.createLogger("WeakClosure")

expect inline fun <T : ViewModel> T.weakSelfClosure(crossinline closure: (T) -> Unit): () -> Unit
expect inline fun <T : ViewModel, V> T.weakSelfClosure(crossinline closure: (vm: T, V) -> Unit): (V) -> Unit
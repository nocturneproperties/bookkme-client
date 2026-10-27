package me.bookk.core.presentation.memory

import me.bookk.core.Logger
import me.bookk.core.presentation.ViewModel

val closureLogger = Logger.create("WeakClosure")

//ARM memory management is not the same as JVM, root context in kotlin is captured by hard references
//So Swift's reference counting system will not release objects that are captured by kotlin lambda's
//even if root node is unreachable as in JVM GC, so extra protection is needed

expect inline fun <T : ViewModel> T.weakVMClosure(crossinline closure: (T) -> Unit): () -> Unit
expect inline fun <T : ViewModel, V> T.weakVMClosure(crossinline closure: (vm: T, V) -> Unit): (V) -> Unit

expect inline fun <T: Any> T.weakSelfClosure(crossinline closure: (T) -> Unit): () -> Unit
expect inline fun <T: Any, V> T.weakSelfClosure(crossinline closure: (vm: T, V) -> Unit): (V) -> Unit
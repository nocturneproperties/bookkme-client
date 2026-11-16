package me.bookk.core.presentation.memory

import me.bookk.core.presentation.ViewModel

actual inline fun <T : ViewModel> T.weakVMClosure(crossinline closure: (T) -> Unit): () -> Unit {
    val weakSelf = WeakRef(this)
    return { weakSelf.deref()?.let { closure(it) } }
}

actual inline fun <T : ViewModel, V> T.weakVMClosure(
    crossinline closure: (vm: T, V) -> Unit,
): (V) -> Unit {
    val weakSelf = WeakRef(this)
    return { arg1 -> weakSelf.deref()?.let { closure(it, arg1) } }
}

actual inline fun <T : Any> T.weakSelfClosure(crossinline closure: (T) -> Unit): () -> Unit {
    val weakSelf = WeakRef(this)
    return { weakSelf.deref()?.let { closure(it) } }
}

actual inline fun <T : Any, V> T.weakSelfClosure(
    crossinline closure: (vm: T, V) -> Unit,
): (V) -> Unit {
    val weakSelf = WeakRef(this)
    return { arg1 -> weakSelf.deref()?.let { closure(it, arg1) } }
}

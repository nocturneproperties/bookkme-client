@file:OptIn(ExperimentalNativeApi::class)

package me.bookk.core.presentation.memory

import me.bookk.core.presentation.ViewModel
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

actual inline fun <T : ViewModel> T.weakSelfClosure(
    crossinline closure: (T) -> Unit
): () -> Unit {
    val weakSelf = WeakReference(this)
    return {
        weakSelf.get()?.let { closure(it) } ?: run {
            closureLogger.i("Weak closure released, ignoring action.")
        }
    }
}

actual inline fun <T : ViewModel, V> T.weakSelfClosure(
    crossinline closure: (vm: T, V) -> Unit
): (V) -> Unit {
    val weakSelf = WeakReference(this)
    return { arg1 ->
        weakSelf.get()?.let { closure(it, arg1) } ?: run {
            closureLogger.i("Weak closure released, ignoring $arg1")
        }
    }
}
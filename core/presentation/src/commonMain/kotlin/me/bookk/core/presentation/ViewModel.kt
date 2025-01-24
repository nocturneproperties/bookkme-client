package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import me.bookk.core.presentation.error.ErrorMapper
import kotlin.coroutines.CoroutineContext

expect abstract class ViewModel(
    vmArgs: VmArgs
) {
    protected val viewModelScope: CoroutineScope
    protected val mapper: ErrorMapper
    protected open val viewModelScopeErrorHandler: CoroutineExceptionHandler

    open fun clear()

    open fun handleError(throwable: Throwable)

    protected fun <Output> launch(
        launchIn: CoroutineContext,
        call: suspend () -> Output,
        onComplete: suspend (Output) -> Unit,
        onError: (suspend (Throwable) -> Unit)? = null,
        onStart: (suspend () -> Unit)? = null,
        onTerminate: (suspend () -> Unit)? = null,
    ): Job
}
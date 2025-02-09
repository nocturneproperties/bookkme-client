package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bookk.core.DispatcherProvider
import me.bookk.core.LogFactory
import me.bookk.core.UsedInSwift
import me.bookk.core.presentation.error.ErrorMapper
import kotlin.coroutines.CoroutineContext

actual abstract class ViewModel actual constructor(
    vmArgs: VmArgs
) {
    private val internalLogger = LogFactory.forName("ViewModel")
    protected actual val mapper: ErrorMapper = vmArgs.errorMapper
    protected actual val viewModelScope = CoroutineScope(SupervisorJob() + DispatcherProvider.main)
    protected actual open val viewModelScopeErrorHandler =
        CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        }

    actual open fun handleError(throwable: Throwable) {
        internalLogger.e(throwable)
    }

    actual fun <Output> launch(
        launchIn: CoroutineContext,
        call: suspend () -> Output,
        onComplete: (suspend (Output) -> Unit)?,
        onError: (suspend (Throwable) -> Unit)?,
        onStart: (suspend () -> Unit)?,
        onTerminate: (suspend () -> Unit)?,
    ): Job {
        return viewModelScope.launch(viewModelScopeErrorHandler) {
            try {
                onStart?.invoke()

                val result = withContext(launchIn) {
                    call()
                }
                onComplete?.invoke(result)
            } catch (e: Throwable) {
                onError?.invoke(e) ?: throw e
            } finally {
                onTerminate?.invoke()
            }
        }
    }

    @UsedInSwift
    actual open fun onViewPresented() {
    }

    @UsedInSwift
    actual open fun onViewHidden() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}
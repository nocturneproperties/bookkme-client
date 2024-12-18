package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bookk.core.DispatcherProvider
import me.bookk.core.Logger
import me.bookk.core.UsedInSwift
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationError
import kotlin.coroutines.CoroutineContext

actual abstract class ViewModel actual constructor(
    vmArgs: VmArgs
) {
    protected actual val logger: Logger = vmArgs.logger
    protected actual val mapper: ErrorMapper = vmArgs.errorMapper
    protected actual val viewModelScope = CoroutineScope(SupervisorJob() + DispatcherProvider.main)
    protected actual open val viewModelScopeErrorHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }
    actual val errorFlow = MutableSharedFlow<PresentationError>()

    actual open fun handleError(throwable: Throwable) {
        logger.e(throwable)
        viewModelScope.launch {
            errorFlow.emit(mapper.mapToError(throwable))
        }
    }

    actual fun <Output> launch(
        launchIn: CoroutineContext,
        call: suspend () -> Output,
        onComplete: suspend (Output) -> Unit,
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
                onComplete.invoke(result)
            } catch (e: Throwable) {
                onError?.invoke(e) ?: throw e
            } finally {
                onTerminate?.invoke()
            }
        }
    }

    @UsedInSwift
    actual open fun clear() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}
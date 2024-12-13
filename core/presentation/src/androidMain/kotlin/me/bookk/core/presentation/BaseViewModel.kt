package me.bookk.core.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bookk.core.Logger
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationError
import kotlin.coroutines.CoroutineContext
import androidx.lifecycle.viewModelScope as frameworkScope

actual abstract class BaseViewModel actual constructor(
    vmArgs: VmArgs
) : ViewModel() {

    protected actual val logger: Logger = vmArgs.logger
    protected actual val viewModelScope = frameworkScope
    protected actual val mapper: ErrorMapper = vmArgs.errorMapper
    protected actual open val viewModelScopeErrorHandler =
        CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        }
    actual val errorFlow = MutableSharedFlow<PresentationError>()

    actual open fun handleError(throwable: Throwable) {
        viewModelScope.launch {
            logger.e(throwable)
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

    actual open fun clear() {
        super.onCleared()
    }
}
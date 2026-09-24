package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import me.bookk.core.Logger
import me.bookk.core.presentation.error.ErrorDescription
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.flow.RetryPolicy
import me.bookk.core.presentation.flow.observeResiliently
import me.bookk.core.presentation.flow.retryingWithBackoff
import me.bookk.core.presentation.flow.safeOnEach
import kotlin.coroutines.CoroutineContext
import androidx.lifecycle.ViewModel as AndroidViewModel
import androidx.lifecycle.viewModelScope as frameworkScope

actual abstract class ViewModel actual constructor(
    vmArgs: VmArgs
) : AndroidViewModel() {

    private val internalLogger = Logger.create("ViewModel")
    private val activeJobs = mutableMapOf<String, Job>()
    protected actual val viewModelScope: CoroutineScope = frameworkScope + CoroutineExceptionHandler { context, throwable ->
        viewModelScopeErrorHandler.handleException(context, throwable)
    }
    protected actual val errorMapper: ErrorMapper = vmArgs.errorMapper
    protected actual open val viewModelScopeErrorHandler =
        CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        }

    actual open fun handleError(throwable: Throwable) {
        internalLogger.e(throwable)
    }

    actual fun <Output> launch(
        key: String?,
        launchBehaviour: LaunchBehaviour,
        launchIn: CoroutineContext,
        call: suspend () -> Output,
        onComplete: (suspend (Output) -> Unit)?,
        onError: (suspend (Throwable) -> Unit),
        onStart: (suspend () -> Unit)?,
        onTerminate: (suspend () -> Unit)?,
    ): Job? {
        val activeJob = when (launchBehaviour) {
            LaunchBehaviour.DropLatest -> {
                if (key != null && activeJobs[key]?.isActive == true) return null
                null
            }

            LaunchBehaviour.DropOldest -> {
                activeJobs[key]
            }
        }
        return viewModelScope.launch(viewModelScopeErrorHandler) {
            try {
                activeJob?.cancelAndJoin()
                onStart?.invoke()

                val result = withContext(launchIn) {
                    call()
                }
                onComplete?.invoke(result)
            } catch (e: Throwable) {
                onError.invoke(e)
            } finally {
                onTerminate?.invoke()
            }
        }.also {
            if (key != null) {
                activeJobs[key] = it
            }
        }
    }

    actual fun <T> Flow<T>.safeOnEach(action: suspend (T) -> Unit): Flow<T> {
        return safeOnEach(logError = ::handleError, action = action)
    }

    actual fun <T> Flow<T>.onError(action: suspend (Throwable) -> Unit): Flow<T> {
        return retryingWithBackoff(
            retryPolicy = RetryPolicy.Default,
            logError = ::handleError,
            onError = action,
        )
    }

    actual fun <T> Flow<T>.observe(): Job {
        return observeResiliently(
            scope = viewModelScope,
            retryPolicy = RetryPolicy.Default,
            logError = ::handleError,
        )
    }

    actual fun Throwable.notification(): PresentationNotification {
        return errorMapper.mapToNotification(this)
    }

    actual fun Throwable.description(): ErrorDescription {
        return errorMapper.mapToDescription(this)
    }

    actual open fun onViewPresented() {
    }

    actual open fun onViewHidden() {
    }

    actual override fun onCleared() {
        internalLogger.i("On clear called $this")
        super.onCleared()
    }
}
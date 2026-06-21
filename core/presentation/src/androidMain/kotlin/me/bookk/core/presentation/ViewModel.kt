package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bookk.core.Logger
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationNotification
import kotlin.coroutines.CoroutineContext
import androidx.lifecycle.ViewModel as AndroidViewModel
import androidx.lifecycle.viewModelScope as frameworkScope

actual abstract class ViewModel actual constructor(
    vmArgs: VmArgs
) : AndroidViewModel() {

    private val internalLogger = Logger.create("ViewModel")
    private val activeJobs = mutableMapOf<String, Job>()
    protected actual val viewModelScope = frameworkScope
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

    actual fun <Output> launchCached(
        key: String?,
        launchBehaviour: LaunchBehaviour,
        launchIn: CoroutineContext,
        call: suspend (suspend (Output) -> Unit) -> Unit,
        onComplete: (suspend (Output) -> Unit),
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

                withContext(launchIn) {
                    call(onComplete)
                }
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

    actual fun Throwable.notification(): PresentationNotification {
        return errorMapper.mapToNotification(this)
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
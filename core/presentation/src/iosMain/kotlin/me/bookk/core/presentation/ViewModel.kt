package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bookk.core.LogFactory
import me.bookk.core.UsedInSwift
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationNotification
import kotlin.coroutines.CoroutineContext

actual abstract class ViewModel actual constructor(
    vmArgs: VmArgs
) {
    private val internalLogger = LogFactory.createLogger("ViewModel")
    private val activeJobs = mutableMapOf<String, Job>()
    protected actual val errorMapper: ErrorMapper = vmArgs.errorMapper
    protected actual val viewModelScope = CoroutineScope(SupervisorJob() + DispatcherProvider.main)
    protected actual open val viewModelScopeErrorHandler =
        CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        }

    init {
        internalLogger.i("Init called $this")
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

    @UsedInSwift
    actual open fun onViewPresented() {
    }

    @UsedInSwift
    actual open fun onViewHidden() {
    }

    @UsedInSwift
    actual fun onCleared() {
        internalLogger.i("On clear called $this")
        viewModelScope.cancel()
    }
}

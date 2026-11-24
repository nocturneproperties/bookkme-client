package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import me.bookk.core.Logger
import me.bookk.core.UsedInSwift
import me.bookk.core.presentation.error.ErrorDescription
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.flow.RetryPolicy
import me.bookk.core.presentation.flow.observeResiliently
import me.bookk.core.presentation.flow.retryingWithBackoff
import me.bookk.core.presentation.flow.safeOnEach
import kotlin.coroutines.CoroutineContext

abstract class ViewModel(
    vmArgs: VmArgs,
) : PlatformViewModel() {
    private val internalLogger = Logger.create("ViewModel")
    private val activeJobs = mutableMapOf<String, Job>()
    protected val errorMapper: ErrorMapper = vmArgs.errorMapper
    protected val viewModelScope: CoroutineScope =
        platformScope + CoroutineExceptionHandler { context, throwable ->
            viewModelScopeErrorHandler.handleException(context, throwable)
        }
    protected open val viewModelScopeErrorHandler =
        CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        }

    init {
        internalLogger.i("Init called $this")
    }

    open fun handleError(throwable: Throwable) {
        internalLogger.e(throwable)
    }

    fun <Output> launch(
        key: String? = null,
        launchBehaviour: LaunchBehaviour = LaunchBehaviour.DropOldest,
        launchIn: CoroutineContext,
        call: suspend () -> Output,
        onComplete: (suspend (Output) -> Unit)? = null,
        onError: (suspend (Throwable) -> Unit),
        onStart: (suspend () -> Unit)? = null,
        onTerminate: (suspend () -> Unit)? = null,
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
                ensureActive()
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

    fun <T> Flow<T>.safeOnEach(action: suspend (T) -> Unit): Flow<T> {
        return safeOnEach(logError = ::handleError, action = action)
    }

    fun <T> Flow<T>.onError(action: suspend (Throwable) -> Unit): Flow<T> {
        return retryingWithBackoff(
            retryPolicy = RetryPolicy.Default,
            logError = ::handleError,
            onError = action,
        )
    }

    fun <T> Flow<T>.observe(): Job {
        return observeResiliently(
            scope = viewModelScope,
            retryPolicy = RetryPolicy.Default,
            logError = ::handleError,
        )
    }

    fun Throwable.notification(): PresentationNotification {
        return errorMapper.mapToNotification(this)
    }

    fun Throwable.description(): ErrorDescription {
        return errorMapper.mapToDescription(this)
    }

    @UsedInSwift
    open fun onViewPresented() {
    }

    @UsedInSwift
    open fun onViewHidden() {
    }
}

enum class LaunchBehaviour {

    /**
     * Drops newest task forever.
     *
     * val job1 = launch(
     *  key = "test",
     *  launchBehaviour = LaunchBehaviour.DROP_LATEST
     *  ...
     * )
     *
     * val job2 = launch(
     * key = "test",
     * launchBehaviour = LaunchBehaviour.DROP_LATEST
     * ...
     * )
     *
     * If job2 will be launched before job1 completes,
     * job2 will never be executed and will be canceled immediately,
     * returning null instead of a job.
     * */
    DropLatest,

    /**
     * Cancels execution of oldest task.
     *
     * val job1 = launch(
     *  key = "test",
     *  launchBehaviour = LaunchBehaviour.DROP_OLDEST
     *  ...
     * )
     *
     * val job2 = launch(
     * key = "test",
     * launchBehaviour = LaunchBehaviour.DROP_OLDEST
     * ...
     * )
     *
     * If job2 will be launched before job1 completes,
     * job1 will be canceled and job2 will be started instead.
     * */
    DropOldest,
}

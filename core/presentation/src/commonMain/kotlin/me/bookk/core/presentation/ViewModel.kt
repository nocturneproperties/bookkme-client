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
    protected val errorMapper: ErrorMapper
    protected open val viewModelScopeErrorHandler: CoroutineExceptionHandler

    open fun onViewPresented()
    open fun onViewHidden()

    open fun handleError(throwable: Throwable)

    protected fun onCleared()

    protected fun <Output> launch(
        key: String? = null,
        launchBehaviour: LaunchBehaviour = LaunchBehaviour.DropOldest,
        launchIn: CoroutineContext,
        call: suspend () -> Output,
        onComplete: (suspend (Output) -> Unit)? = null,
        onError: (suspend (Throwable) -> Unit)? = null,
        onStart: (suspend () -> Unit)? = null,
        onTerminate: (suspend () -> Unit)? = null,
    ): Job?
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
     * job2 will never be executed and will be cancelled immediately,
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
     * job1 will be cancelled and job2 will be started instead.
     * */
    DropOldest
}
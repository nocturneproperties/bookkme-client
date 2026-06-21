package me.bookk.core.coroutine

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.bookk.core.Logger

/**
 * Scope that is used for background screen independent job such as caching, downloading big files etc
 * */
fun createApplicationScope(): CoroutineScope {
    val logger = Logger.create("ApplicationScope")
    return CoroutineScope(
        SupervisorJob() +
                DispatcherProvider.default +
                CoroutineExceptionHandler { _, throwable ->
                    logger.e(throwable)
                }
    )
}
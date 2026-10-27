package me.bookk.core.presentation.flow

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal data class RetryPolicy(
    val initialDelay: Duration,
    val maxDelay: Duration,
) {
    fun delayAfter(consecutiveFailures: Int): Duration {
        var delay = initialDelay
        repeat(consecutiveFailures - 1) {
            delay = (delay * 2).coerceAtMost(maxDelay)
        }
        return delay.coerceAtMost(maxDelay)
    }

    companion object {
        val Default = RetryPolicy(initialDelay = 1.seconds, maxDelay = 30.seconds)
    }
}

internal fun <T> Flow<T>.safeOnEach(
    logError: (Throwable) -> Unit,
    action: suspend (T) -> Unit,
): Flow<T> = onEach { value -> runSafely(value, logError, action) }

internal fun <T> Flow<T>.observeResiliently(
    scope: CoroutineScope,
    retryPolicy: RetryPolicy,
    logError: (Throwable) -> Unit,
): Job {
    return retryingWithBackoff(retryPolicy, logError, onError = {})
        .launchIn(scope)
}

internal fun <T> Flow<T>.retryingWithBackoff(
    retryPolicy: RetryPolicy,
    logError: (Throwable) -> Unit,
    onError: suspend (Throwable) -> Unit,
): Flow<T> = flow {
    var consecutiveFailures = 0
    emitAll(
        onEach { consecutiveFailures = 0 }
            .retryWhen { cause, _ ->
                if (cause is CancellationException) return@retryWhen false
                consecutiveFailures++
                logError(cause)
                if (consecutiveFailures == 1) onError(cause)
                delay(retryPolicy.delayAfter(consecutiveFailures))
                true
            }
    )
}

private suspend fun <T> runSafely(
    value: T,
    logError: (Throwable) -> Unit,
    action: suspend (T) -> Unit,
) {
    try {
        action(value)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        logError(e)
    }
}

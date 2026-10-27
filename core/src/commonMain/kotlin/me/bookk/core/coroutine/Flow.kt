package me.bookk.core.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration

fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}

fun countdownFlow(totalSeconds: Int): Flow<Int> = flow {
    for (remainingSeconds in totalSeconds downTo 0) {
        emit(remainingSeconds)
        delay(1000L)
    }
}

fun <T> Flow<Result<T>>.resultOnEach(action: suspend (T) -> Unit): Flow<Result<T>> {
    return onEach {
        it.onSuccess {
            action(it)
        }
    }
}

fun <T> Flow<Result<T>>.resultOnError(action: suspend (Throwable) -> Unit): Flow<Result<T>> {
    return onEach {
        it.onFailure {
            action(it)
        }
    }
}

inline fun <T : Any, R> Flow<T?>.flatMapLatestOrNull(
    crossinline transform: suspend (T) -> Flow<R>
): Flow<R?> = flatMapLatest { value ->
    if (value != null) transform(value) else flowOf(null)
}

inline fun <T : Any, R> Flow<T?>.mapOrNull(
    crossinline transform: suspend (T) -> R
): Flow<R?> = map { value ->
    if (value != null) transform(value) else null
}
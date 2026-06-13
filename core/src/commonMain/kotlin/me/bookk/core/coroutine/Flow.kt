package me.bookk.core.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
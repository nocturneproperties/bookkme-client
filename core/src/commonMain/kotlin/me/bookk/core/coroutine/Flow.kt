package me.bookk.core.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
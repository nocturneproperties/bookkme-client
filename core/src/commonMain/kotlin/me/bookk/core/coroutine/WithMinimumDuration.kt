package me.bookk.core.coroutine

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

suspend fun <T> withMinimumDuration(
    minimumMillis: Duration = 0.milliseconds,
    block: suspend () -> T
): T {
    val (result, duration) = measureTimedValue { block() }
    val remaining = minimumMillis.inWholeMilliseconds - duration.inWholeMilliseconds
    if (remaining > 0) delay(remaining)
    return result
}
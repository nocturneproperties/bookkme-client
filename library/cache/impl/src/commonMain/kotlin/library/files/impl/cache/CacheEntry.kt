package library.files.impl.cache

import kotlin.time.Duration
import kotlin.time.TimeSource

internal class CacheEntry<T>(
    val data: T,
    duration: Duration
) {
    val expirationMark = TimeSource.Monotonic.markNow() + duration
}
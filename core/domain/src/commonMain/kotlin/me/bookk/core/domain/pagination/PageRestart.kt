package me.bookk.core.domain.pagination

import me.bookk.core.domain.entity.Error

suspend fun <T> fetchPageOrRestart(
    fetch: suspend () -> Page<T>,
    restart: suspend () -> Page<T>,
): Page<T> {
    return try {
        fetch()
    } catch (error: Error.BusinessError) {
        if (error.errorCode != PageErrorCodes.CURSOR_INVALID) throw error
        restart()
    }
}

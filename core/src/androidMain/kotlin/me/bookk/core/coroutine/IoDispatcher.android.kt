package me.bookk.core.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual fun ioDispatcher(): CoroutineDispatcher {
    return Dispatchers.IO
}

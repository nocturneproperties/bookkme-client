package me.bookk.feature.business.domain.api.entity

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.coroutine.DispatcherProvider

interface BusinessEvent {
    data object PluginStateChanged : BusinessEvent
}

val businessEvents = MutableSharedFlow<BusinessEvent>(
    extraBufferCapacity = 100,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

inline fun <reified T : BusinessEvent> listenFor(noinline onEach: suspend (T) -> Unit): Flow<T> {
    return businessEvents
        .filterIsInstance<T>()
        .flowOn(DispatcherProvider.default)
        .onEach(onEach)
        .flowOn(DispatcherProvider.main)
}
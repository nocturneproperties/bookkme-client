package me.bookk.feature.services.domain.api.group

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup

sealed interface ServiceGroupEvent {
    data class Created(val group: ServiceGroup) : ServiceGroupEvent
    data class Deleted(val group: ServiceGroup) : ServiceGroupEvent
}

val serviceGroupEvents = MutableSharedFlow<ServiceGroupEvent>(
    extraBufferCapacity = 100,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

inline fun <reified T : ServiceGroupEvent> listenFor(noinline onEach: suspend (T) -> Unit): Flow<T> {
    return serviceGroupEvents
        .filterIsInstance<T>()
        .flowOn(DispatcherProvider.default)
        .onEach(onEach)
        .flowOn(DispatcherProvider.main)
}
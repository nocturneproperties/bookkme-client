package me.bookk.feature.services.domain.api.service

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.feature.services.domain.api.service.entity.Service

sealed interface ServiceEvent {
    data class Created(val service: Service) : ServiceEvent
    data class Deleted(val service: Service) : ServiceEvent
}

val serviceEvents = MutableSharedFlow<ServiceEvent>(extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)

inline fun <reified T : ServiceEvent> listenFor(noinline onEach: suspend (T) -> Unit): Flow<T> {
    return serviceEvents
        .filterIsInstance<T>()
        .flowOn(DispatcherProvider.default)
        .onEach(onEach)
        .flowOn(DispatcherProvider.main)
}
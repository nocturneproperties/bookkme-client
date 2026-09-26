package me.bookk.feature.clients.domain.api.entity

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance

sealed interface ClientEvent {
    data class Created(val client: Client) : ClientEvent
    data class Updated(val client: Client) : ClientEvent
    data class Deleted(val client: Client) : ClientEvent
}

val clientEvents = MutableSharedFlow<ClientEvent>(extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)

inline fun <reified T : ClientEvent> listenFor(): Flow<T> {
    return clientEvents
        .filterIsInstance<T>()
}
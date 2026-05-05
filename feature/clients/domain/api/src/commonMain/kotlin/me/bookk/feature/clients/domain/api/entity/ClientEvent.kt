package me.bookk.feature.clients.domain.api.entity

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface ClientEvent {
    data class Created(val client: Client) : ClientEvent
}

val clientEvents = Channel<ClientEvent>()

inline fun <reified T : ClientEvent> listenFor(): Flow<T> {
    return clientEvents
        .receiveAsFlow()
        .filterIsInstance<T>()
}
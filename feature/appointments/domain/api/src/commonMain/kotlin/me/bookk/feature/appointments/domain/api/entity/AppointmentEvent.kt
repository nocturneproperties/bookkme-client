package me.bookk.feature.appointments.domain.api.entity

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.coroutine.DispatcherProvider

interface AppointmentEvent {
    data class Created(val appointment: Appointment) : AppointmentEvent
    data class Cancelled(val appointment: Appointment) : AppointmentEvent
}

val appointmentEvents = MutableSharedFlow<AppointmentEvent>(
    extraBufferCapacity = 100,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

inline fun <reified T : AppointmentEvent> listenFor(noinline onEach: suspend (T) -> Unit): Flow<T> {
    return appointmentEvents
        .filterIsInstance<T>()
        .flowOn(DispatcherProvider.default)
        .onEach(onEach)
        .flowOn(DispatcherProvider.main)
}
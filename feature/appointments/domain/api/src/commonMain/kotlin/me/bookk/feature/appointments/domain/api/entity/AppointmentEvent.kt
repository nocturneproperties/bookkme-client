package me.bookk.feature.appointments.domain.api.entity

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance

interface AppointmentEvent {
    data class Created(val appointment: Appointment) : AppointmentEvent
    data class Cancelled(val appointment: Appointment) : AppointmentEvent
    data class Updated(val appointment: Appointment) : AppointmentEvent
}

val appointmentEvents = MutableSharedFlow<AppointmentEvent>(
    extraBufferCapacity = 100,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

inline fun <reified T : AppointmentEvent> listenFor(): Flow<T> {
    return appointmentEvents
        .filterIsInstance<T>()
}
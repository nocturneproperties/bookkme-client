package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface MarkAppointmentNoShow {
    suspend operator fun invoke(appointmentId: Uuid): Appointment

    sealed interface Error {
        class AppointmentAlreadyCancelled : Throwable(), Error
        class AppointmentNotStarted : Throwable(), Error
    }
}

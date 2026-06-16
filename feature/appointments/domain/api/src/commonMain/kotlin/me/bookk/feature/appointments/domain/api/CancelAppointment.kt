package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface CancelAppointment {
    suspend operator fun invoke(appointmentId: Uuid, businessId: Uuid, reason: String): Appointment

    sealed interface Error {
        class AppointmentAlreadyCancelled : Throwable(), Error
        class AppointmentAlreadyCompleted : Throwable(), Error
    }
}

package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface ApproveAppointmentRequest {
    suspend operator fun invoke(requestId: Uuid): Appointment

    sealed interface Error {
        class AppointmentExists : Throwable(), Error
        class DateNotAllowed : Throwable(), Error
        class TimeNotAllowed : Throwable(), Error
        class DateInPast : Throwable(), Error
    }
}

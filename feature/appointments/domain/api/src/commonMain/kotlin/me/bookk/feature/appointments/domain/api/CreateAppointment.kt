package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentDraft

interface CreateAppointment {
    suspend operator fun invoke(draft: AppointmentDraft): Appointment

    sealed interface Error {
        class DateIsNotAllowed : Throwable(), Error
        class TimeIsNotAllowed : Throwable(), Error
        class AppointmentOverlap : Throwable(),Error
    }
}

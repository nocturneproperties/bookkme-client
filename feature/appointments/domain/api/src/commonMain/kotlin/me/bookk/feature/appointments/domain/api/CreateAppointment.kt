package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentDraft

interface CreateAppointment {
    suspend operator fun invoke(draft: AppointmentDraft): Appointment

    sealed interface Error {
        class PriceChanged :
            Exception(),
            Error
        class DurationChanged :
            Exception(),
            Error
        class ServicesChanged :
            Exception(),
            Error
        class ServiceNotFound :
            Exception(),
            Error
        class EmptyServices :
            Exception(),
            Error
        class DateInPast :
            Exception(),
            Error
        class OfferAlreadyUsed :
            Exception(),
            Error
        class EmployeeNotFound :
            Exception(),
            Error
        class ClientNotFound :
            Exception(),
            Error
        class EmployeeSuspended :
            Exception(),
            Error
        class DateIsNotAllowed :
            Throwable(),
            Error
        class TimeIsNotAllowed :
            Throwable(),
            Error
        class AppointmentOverlap :
            Throwable(),
            Error
        class NotAnEmployee :
            Throwable(),
            Error
    }
}

package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment

interface UpdateAppointment {
    suspend operator fun invoke(appointment: Appointment): Appointment

    sealed interface Error {
        class DateInPast :
            Throwable(),
            Error
        class InvalidServiceSelection :
            Throwable(),
            Error
        class ServiceNotFound :
            Throwable(),
            Error
        class EmployeeNotFound :
            Throwable(),
            Error
        class ConcurrentModification :
            Throwable(),
            Error
        class EditWindowClosed :
            Throwable(),
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
        class AppointmentNotScheduled :
            Throwable(),
            Error
        class EmployeeSuspended :
            Throwable(),
            Error
    }
}

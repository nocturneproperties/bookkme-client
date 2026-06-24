package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings

interface UpdateAppointmentSettings {
    suspend operator fun invoke(settings: AppointmentSettings): AppointmentSettings

    sealed interface Error {
        class ActiveDayWithoutWorkHours : Throwable(), Error
        class InvalidDayOffRange : Throwable(), Error
    }
}

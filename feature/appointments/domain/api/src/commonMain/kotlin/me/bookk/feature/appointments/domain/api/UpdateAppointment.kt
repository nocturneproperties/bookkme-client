package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment

interface UpdateAppointment {
    suspend operator fun invoke(appointment: Appointment): Appointment
}

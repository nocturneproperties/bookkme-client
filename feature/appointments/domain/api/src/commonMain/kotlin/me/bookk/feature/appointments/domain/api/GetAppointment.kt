package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface GetAppointment {
    suspend operator fun invoke(id: Uuid): Appointment
}

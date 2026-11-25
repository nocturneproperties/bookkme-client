package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface GetClientAppointment {
    fun flow(appointmentId: Uuid): Flow<Appointment?>
}

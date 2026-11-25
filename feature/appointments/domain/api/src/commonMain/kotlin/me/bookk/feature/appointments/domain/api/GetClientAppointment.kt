package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.ClientAppointment
import kotlin.uuid.Uuid

interface GetClientAppointment {
    fun flow(appointmentId: Uuid): Flow<ClientAppointment?>
}

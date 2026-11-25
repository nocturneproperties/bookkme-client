package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.ClientAppointmentRequest
import kotlin.uuid.Uuid

interface GetClientAppointmentRequest {
    fun flow(requestId: Uuid): Flow<ClientAppointmentRequest?>
}

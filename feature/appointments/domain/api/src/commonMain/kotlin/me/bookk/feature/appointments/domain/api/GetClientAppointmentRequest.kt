package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.uuid.Uuid

interface GetClientAppointmentRequest {
    fun flow(requestId: Uuid): Flow<AppointmentRequest?>
}

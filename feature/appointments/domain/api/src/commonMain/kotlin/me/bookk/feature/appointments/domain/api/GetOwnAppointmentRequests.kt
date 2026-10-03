package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.uuid.Uuid

interface GetOwnAppointmentRequests {
    fun flow(businessId: Uuid): Flow<List<AppointmentRequest>>
    suspend fun refresh(businessId: Uuid): List<AppointmentRequest>
}

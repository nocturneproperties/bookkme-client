package me.bookk.feature.appointments.domain.datasource

import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.uuid.Uuid

interface AppointmentRequestDataSource {
    suspend fun createAppointmentRequest(request: AppointmentRequest): AppointmentRequest

    suspend fun createAppointmentFromRequest(requestId: Uuid): Appointment

    suspend fun getAppointmentRequests(businessId: Uuid): List<AppointmentRequest>

    suspend fun saveAppointmentRequestsInDB(requests: List<AppointmentRequest>)

    suspend fun declineAppointmentRequest(requestId: Uuid, businessId: Uuid, reason: String)
}

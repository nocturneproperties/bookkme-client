package me.bookk.feature.appointments.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface AppointmentRequestDataSource {
    suspend fun createAppointmentRequest(request: AppointmentRequest, offerToken: String)

    suspend fun createAppointmentFromRequest(requestId: Uuid): Appointment

    suspend fun getAppointmentRequests(businessId: Uuid): List<AppointmentRequest>

    fun observeAppointmentRequestsDBChanges(businessId: Uuid): Flow<List<AppointmentRequest>>

    suspend fun getAppointmentRequestIdsInDb(businessId: Uuid): List<Uuid>

    suspend fun deleteAppointmentRequestsInDb(ids: List<Uuid>)

    suspend fun saveAppointmentRequestsInDB(requests: List<AppointmentRequest>)

    suspend fun declineAppointmentRequest(requestId: Uuid, businessId: Uuid, reason: String)

    suspend fun getLastSyncedAt(businessId: Uuid): Instant?

    suspend fun saveLastSyncedAt(businessId: Uuid)
}

package me.bookk.feature.appointments.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.pagination.Page
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestDraft
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface AppointmentRequestDataSource {
    suspend fun createAppointmentRequest(draft: AppointmentRequestDraft)

    suspend fun createAppointmentFromRequest(requestId: Uuid): Appointment

    suspend fun getAssignedAppointmentRequests(
        businessId: Uuid,
        employeeId: Uuid,
        cursor: String?,
    ): Page<AppointmentRequest>

    fun observeAppointmentRequestsDBChanges(businessId: Uuid): Flow<List<AppointmentRequest>>

    suspend fun getAppointmentRequestIdsInDb(businessId: Uuid): List<Uuid>

    suspend fun deleteAppointmentRequestsInDb(ids: List<Uuid>)

    suspend fun saveAppointmentRequestsInDB(requests: List<AppointmentRequest>)

    suspend fun declineAppointmentRequest(requestId: Uuid, businessId: Uuid, reason: String)

    suspend fun withdrawAppointmentRequest(requestId: Uuid)

    suspend fun saveAppointmentRequestStatusInDb(requestId: Uuid, status: AppointmentRequestStatus)

    suspend fun getLastSyncedAt(businessId: Uuid): Instant?

    suspend fun saveLastSyncedAt(businessId: Uuid)
}

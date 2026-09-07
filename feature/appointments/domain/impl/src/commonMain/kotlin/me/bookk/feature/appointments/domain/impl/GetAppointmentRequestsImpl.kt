package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.GetAppointmentRequests
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.uuid.Uuid

internal class GetAppointmentRequestsImpl(
    private val dataSource: AppointmentRequestDataSource
) : GetAppointmentRequests {

    override fun flow(businessId: Uuid): Flow<List<AppointmentRequest>> {
        return dataSource.observeAppointmentRequestsDBChanges(businessId)
            .map { requests ->
                requests.filter { it.status == AppointmentRequestStatus.PENDING }.sortedBy { it.date }
            }
    }

    override suspend fun refresh(businessId: Uuid): List<AppointmentRequest> {
        val requests = dataSource.getAppointmentRequests(businessId)
        val freshIds = requests.map { it.id }.toSet()
        val staleIds = dataSource.getAppointmentRequestIdsInDb(businessId).filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            dataSource.deleteAppointmentRequestsInDb(staleIds)
        }
        dataSource.saveAppointmentRequestsInDB(requests)
        dataSource.saveLastSyncedAt(businessId)
        return requests
    }
}

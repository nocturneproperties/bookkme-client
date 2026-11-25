package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.fetchPageOrRestart
import me.bookk.feature.appointments.domain.api.GetAssignedAppointmentRequests
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import kotlin.uuid.Uuid

internal class GetAssignedAppointmentRequestsImpl(
    private val dataSource: AppointmentRequestDataSource,
    private val businesses: ObserveUserBusinessesChanges,
) : GetAssignedAppointmentRequests {

    override fun flow(businessId: Uuid): Flow<List<AppointmentRequest>> {
        return dataSource.observeAppointmentRequestsDBChanges(businessId)
            .map { it.pendingByDate() }
    }

    override suspend fun refresh(key: Uuid): Page<AppointmentRequest> {
        val page = fetchPage(key, cursor = null)
        deleteRequestsMissingFrom(page, key)
        dataSource.saveAppointmentRequestsInDB(page.items)
        dataSource.saveLastSyncedAt(key)
        return page
    }

    override suspend fun loadMore(key: Uuid, cursor: String): Page<AppointmentRequest> {
        return fetchPageOrRestart(
            fetch = { appendPage(key, cursor) },
            restart = { refresh(key) },
        )
    }

    private suspend fun appendPage(businessId: Uuid, cursor: String): Page<AppointmentRequest> {
        val page = fetchPage(businessId, cursor)
        dataSource.saveAppointmentRequestsInDB(page.items)
        return page
    }

    private suspend fun fetchPage(businessId: Uuid, cursor: String?): Page<AppointmentRequest> {
        return dataSource.getAssignedAppointmentRequests(
            businessId = businessId,
            employeeId = employeeIdIn(businessId),
            cursor = cursor,
        )
    }

    private suspend fun employeeIdIn(businessId: Uuid): Uuid {
        return businesses().first().firstOrNull { it.id == businessId }?.employeeId
            ?: throw Error.InvalidApplicationState()
    }

    private suspend fun deleteRequestsMissingFrom(
        page: Page<AppointmentRequest>,
        businessId: Uuid,
    ) {
        val freshIds = page.items.map { it.id }.toSet()
        val staleIds = dataSource.getAppointmentRequestIdsInDb(businessId).filterNot {
            it in freshIds
        }
        if (staleIds.isNotEmpty()) {
            dataSource.deleteAppointmentRequestsInDb(staleIds)
        }
    }

    private fun List<AppointmentRequest>.pendingByDate(): List<AppointmentRequest> {
        return filter { it.status == AppointmentRequestStatus.PENDING }.sortedBy { it.date }
    }
}

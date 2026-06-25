package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.pagination.CounterPager
import me.bookk.feature.appointments.domain.api.GetAppointmentHistory
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid

internal class GetAppointmentHistoryImpl(
    private val dataSource: AppointmentDataSource
) : GetAppointmentHistory {

    private val pager = object : CounterPager<HistoryRequest, Appointment>(countPerPage = PAGE_SIZE) {
        override suspend fun loadContent(request: HistoryRequest): List<Appointment> =
            dataSource.getAppointmentHistory(
                businessId = request.businessId,
                limit = countPerPage,
                offset = pageData.loadedItemCount.toLong(),
                query = request.query
            )
    }

    override suspend fun reload(businessId: Uuid, query: String?): List<Appointment> {
        return pager.reload(HistoryRequest(businessId, query))
    }

    override suspend fun loadMore(): List<Appointment> = pager.loadMore()

    private data class HistoryRequest(val businessId: Uuid, val query: String?) : CounterPager.Request

    companion object {
        private const val PAGE_SIZE = 20
    }
}

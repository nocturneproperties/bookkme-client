package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.fetchPageOrRestart
import me.bookk.feature.appointments.domain.api.GetAppointmentHistory
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentHistoryQuery
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid

internal class GetAppointmentHistoryImpl(
    private val dataSource: AppointmentDataSource,
) : GetAppointmentHistory {

    override fun flow(businessId: Uuid, search: String?): Flow<List<Appointment>> {
        val text = search?.trim()?.takeIf { it.isNotEmpty() }
        return dataSource.observeAppointmentsDBChanges(businessId)
            .map { appointments -> appointments.filter { text == null || it.matches(text) } }
    }

    override suspend fun refresh(key: AppointmentHistoryQuery): Page<Appointment> {
        val page = dataSource.getAppointmentHistory(key.businessId, key.search, cursor = null)
        return cachePage(key, page, isFirstPage = true)
    }

    override suspend fun loadMore(key: AppointmentHistoryQuery, cursor: String): Page<Appointment> {
        return fetchPageOrRestart(
            fetch = {
                val page = dataSource.getAppointmentHistory(key.businessId, key.search, cursor)
                cachePage(key, page, isFirstPage = false)
            },
            restart = { refresh(key) },
        )
    }

    private suspend fun cachePage(
        key: AppointmentHistoryQuery,
        page: Page<Appointment>,
        isFirstPage: Boolean,
    ): Page<Appointment> {
        if (key.search == null) {
            deleteMissingWithinPage(key.businessId, page, isFirstPage)
        }
        dataSource.saveAppointmentsInDB(page.items)
        return page
    }

    private suspend fun deleteMissingWithinPage(
        businessId: Uuid,
        page: Page<Appointment>,
        isFirstPage: Boolean,
    ) {
        val range = page.dateRange(isFirstPage) ?: return
        val freshIds = page.items.map { it.id }.toSet()
        val staleIds = dataSource.getAppointmentIdsInDb(businessId, range.oldest, range.newest)
            .filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            dataSource.deleteAppointmentsInDb(staleIds)
        }
    }

    private fun Page<Appointment>.dateRange(isFirstPage: Boolean): DateRange? {
        if (items.isEmpty()) return if (isFirstPage && !hasMore) DateRange(null, null) else null
        return DateRange(
            oldest = if (hasMore) items.last().date else null,
            newest = if (isFirstPage) null else items.first().date,
        )
    }

    private fun Appointment.matches(text: String): Boolean {
        val serviceNames = (services + providedServices).map { it.name }
        return (serviceNames + client.fullName).any { it.contains(text, ignoreCase = true) }
    }

    private class DateRange(
        val oldest: LocalDateTime?,
        val newest: LocalDateTime?,
    )
}

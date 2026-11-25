package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.fetchPageOrRestart
import me.bookk.feature.appointments.domain.api.GetClientBookings
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ClientBooking
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource

internal class GetClientBookingsImpl(
    private val dataSource: ClientBookingDataSource,
) : GetClientBookings {

    override fun flow(period: ClientBookingPeriod): Flow<List<ClientBooking>> {
        return combine(
            dataSource.observeAppointments(period),
            dataSource.observeRequests(period),
        ) { appointments, requests -> BookingLists(appointments, requests).listed(period) }
    }

    override suspend fun refresh(key: ClientBookingPeriod): Page<ClientBooking> {
        return loadPage(key, ClientBookingsCursor.START)
    }

    override suspend fun loadMore(key: ClientBookingPeriod, cursor: String): Page<ClientBooking> {
        val position = ClientBookingsCursor.decode(cursor) ?: return refresh(key)
        return fetchPageOrRestart(
            fetch = { loadPage(key, position) },
            restart = { refresh(key) },
        )
    }

    private suspend fun loadPage(
        period: ClientBookingPeriod,
        cursor: ClientBookingsCursor,
    ): Page<ClientBooking> {
        val merged = MergedBookings(
            period = period,
            coveredUntil = cursor.coveredUntil,
            appointments = cursor.appointments.fetch(Appointment::startsAt) {
                dataSource.getOwnAppointments(period, it)
            },
            requests = cursor.requests.fetch(AppointmentRequest::date) {
                dataSource.getOwnRequests(period, it)
            },
        )
        dataSource.replaceAppointments(period, merged.coveredDates, merged.lists.appointments)
        dataSource.replaceRequests(period, merged.coveredDates, merged.lists.requests)
        return Page(merged.lists.listed(period), merged.nextCursor())
    }
}

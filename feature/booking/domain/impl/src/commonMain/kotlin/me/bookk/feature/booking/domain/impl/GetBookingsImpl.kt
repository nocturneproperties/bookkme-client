package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.bookk.core.domain.pagination.Page
import me.bookk.feature.appointments.domain.api.GetClientBookings
import me.bookk.feature.appointments.domain.api.entity.ClientBooking
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.booking.domain.api.GetBookings
import me.bookk.feature.booking.domain.api.entity.BookingOverview
import me.bookk.feature.booking.domain.api.entity.BookingPeriod

internal class GetBookingsImpl(
    private val getClientBookings: GetClientBookings,
) : GetBookings {

    override fun flow(period: BookingPeriod): Flow<List<BookingOverview>> {
        return getClientBookings.flow(period.toClientPeriod())
            .map { bookings -> bookings.mapNotNull { it.toOverview() } }
            .distinctUntilChanged()
    }

    override suspend fun refresh(key: BookingPeriod): Page<BookingOverview> {
        return getClientBookings.refresh(key.toClientPeriod()).toOverviews()
    }

    override suspend fun loadMore(key: BookingPeriod, cursor: String): Page<BookingOverview> {
        return getClientBookings.loadMore(key.toClientPeriod(), cursor).toOverviews()
    }

    private fun Page<ClientBooking>.toOverviews(): Page<BookingOverview> {
        return Page(items.mapNotNull { it.toOverview() }, nextCursor)
    }

    private fun ClientBooking.toOverview(): BookingOverview? {
        return when (this) {
            is ClientBooking.Visit -> appointment.takeIf { it.services.isNotEmpty() }?.let {
                BookingOverview(
                    id = it.id,
                    summary = it.toVisit().toSummary(),
                    status = BookingOverview.Status.Appointment(it.status.toBookingStatus()),
                )
            }

            is ClientBooking.Request -> request.takeIf { it.services.isNotEmpty() }?.let {
                BookingOverview(
                    id = it.id,
                    summary = it.toVisit().toSummary(),
                    status = BookingOverview.Status.Request(it.status.toBookingStatus()),
                )
            }
        }
    }

    private fun BookingPeriod.toClientPeriod(): ClientBookingPeriod {
        return when (this) {
            BookingPeriod.UPCOMING -> ClientBookingPeriod.UPCOMING
            BookingPeriod.PAST -> ClientBookingPeriod.PAST
        }
    }
}

package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.appointments.domain.datasource.DateBounds
import kotlin.time.Instant

internal class GetUpcomingBookingsImpl(
    private val dataSource: ClientBookingDataSource,
) : GetUpcomingBookings {

    override fun flow(): Flow<List<UpcomingBooking>> {
        return combine(
            dataSource.observeAppointments(ClientBookingPeriod.UPCOMING),
            dataSource.observeRequests(ClientBookingPeriod.UPCOMING),
        ) { appointments, requests -> UpcomingBookings(appointments, requests).toUpcoming() }
    }

    override suspend fun refresh(): List<UpcomingBooking> {
        val bookings = dataSource.getUpcomingBookings()
        dataSource.replaceAppointments(
            ClientBookingPeriod.UPCOMING,
            coveredDates(bookings.appointments.map { it.startsAt() }),
            bookings.appointments,
        )
        dataSource.replaceRequests(
            ClientBookingPeriod.UPCOMING,
            coveredDates(bookings.requests.map { it.date }),
            bookings.requests,
        )
        return bookings.toUpcoming()
    }

    private fun coveredDates(dates: List<Instant>): DateBounds {
        if (dates.size < UPCOMING_LIMIT) return DateBounds()
        return DateBounds(to = dates.max())
    }

    private fun UpcomingBookings.toUpcoming(): List<UpcomingBooking> {
        val confirmed = appointments.map { it.toUpcoming() }
        val waiting = requests.map { it.toUpcoming() }
        return (confirmed + waiting).sortedBy { it.date }
    }

    private fun Appointment.toUpcoming(): UpcomingBooking {
        return UpcomingBooking(
            id = id,
            businessId = businessId,
            businessName = businessName,
            services = services,
            employee = employee,
            date = date,
            status = UpcomingBooking.Status.CONFIRMED,
        )
    }

    private fun AppointmentRequest.toUpcoming(): UpcomingBooking {
        return UpcomingBooking(
            id = id,
            businessId = businessId,
            businessName = businessName,
            services = services,
            employee = employee,
            date = date.toLocalDateTime(TimeZone.currentSystemDefault()),
            status = UpcomingBooking.Status.WAITING_FOR_CONFIRMATION,
        )
    }

    private companion object {
        const val UPCOMING_LIMIT = 2
    }
}

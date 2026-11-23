package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.business.domain.api.business.GetBusinesses
import kotlin.uuid.Uuid

internal class GetUpcomingBookingsImpl(
    private val dataSource: ClientBookingDataSource,
    private val getBusinesses: GetBusinesses,
) : GetUpcomingBookings {

    override fun flow(): Flow<List<UpcomingBooking>> {
        return combine(
            dataSource.observeUpcomingAppointments(),
            dataSource.observeUpcomingRequests(),
            ::UpcomingBookings,
        ).flatMapLatest { bookings ->
            getBusinesses.flow(bookings.businessIds)
                .map { businesses -> bookings.toUpcoming(businesses.names()) }
        }
    }

    override suspend fun refresh(): List<UpcomingBooking> {
        val bookings = dataSource.getUpcomingBookings()
        val businesses = getBusinesses.refresh(bookings.businessIds)
        dataSource.saveUpcomingBookingsInDb(bookings)
        return bookings.toUpcoming(businesses.names())
    }

    private fun UpcomingBookings.toUpcoming(names: Map<Uuid, String>): List<UpcomingBooking> {
        val confirmed = appointments.map { it.toUpcoming(names[it.businessId].orEmpty()) }
        val waiting = requests.map { it.toUpcoming(names[it.businessId].orEmpty()) }
        return (confirmed + waiting).sortedBy { it.date }
    }

    private fun Appointment.toUpcoming(businessName: String): UpcomingBooking {
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

    private fun AppointmentRequest.toUpcoming(businessName: String): UpcomingBooking {
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
}

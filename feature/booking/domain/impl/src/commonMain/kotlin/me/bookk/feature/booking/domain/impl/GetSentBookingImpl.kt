package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking
import me.bookk.feature.booking.domain.api.GetSentBooking
import me.bookk.feature.booking.domain.api.entity.SentBooking
import me.bookk.feature.booking.domain.api.entity.SentBooking.BookedService
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import kotlin.time.Duration
import kotlin.time.Instant

internal class GetSentBookingImpl(
    private val getUpcomingBookings: GetUpcomingBookings,
) : GetSentBooking {

    override fun flow(request: SentBookingRequest): Flow<SentBooking?> {
        return getUpcomingBookings.flow()
            .map { bookings -> bookings.firstOrNull { it.isSent(request) }?.toSentBooking(request) }
            .distinctUntilChanged()
    }

    private fun UpcomingBooking.isSent(request: SentBookingRequest): Boolean {
        return businessId == request.businessId &&
            employee.id == request.specialistId &&
            date == request.startsAt.toLocalTime()
    }

    private fun UpcomingBooking.toSentBooking(request: SentBookingRequest): SentBooking {
        val duration = services.fold(Duration.ZERO) { total, service -> total + service.duration }
        return SentBooking(
            id = id,
            startsAt = date,
            endsAt = (request.startsAt + duration).toLocalTime(),
            services = services.booked(),
            status = status.toSentStatus(),
        )
    }

    private fun List<ServiceSnapshot>.booked(): List<BookedService> {
        return groupBy { it.id }.values.map { BookedService(it.first().name, it.size) }
    }

    private fun UpcomingBooking.Status.toSentStatus(): SentBooking.Status {
        return when (this) {
            UpcomingBooking.Status.WAITING_FOR_CONFIRMATION -> {
                SentBooking.Status.WAITING_FOR_CONFIRMATION
            }

            UpcomingBooking.Status.CONFIRMED -> {
                SentBooking.Status.CONFIRMED
            }
        }
    }

    private fun Instant.toLocalTime(): LocalDateTime {
        return toLocalDateTime(TimeZone.currentSystemDefault())
    }
}

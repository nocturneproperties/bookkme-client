package me.bookk.feature.booking.domain.impl

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import library.money.api.Money
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.booking.domain.api.entity.BookingSummary
import me.bookk.feature.booking.domain.api.entity.BookingSummary.BookedService
import kotlin.time.Duration
import kotlin.time.Instant

internal class BookedVisit(
    val businessName: String,
    val employee: EmployeeSnapshot,
    val startsAt: Instant,
    val duration: Duration,
    val services: List<ServiceSnapshot>,
    val total: Money,
    val note: String,
)

internal fun BookedVisit.toSummary(): BookingSummary {
    val timeZone = TimeZone.currentSystemDefault()
    return BookingSummary(
        businessName = businessName,
        specialistName = employee.fullName,
        startsAt = startsAt.toLocalDateTime(timeZone),
        endsAt = (startsAt + duration).toLocalDateTime(timeZone),
        note = note,
        services = services.booked(),
        total = total,
    )
}

internal fun List<ServiceSnapshot>.totalDuration(): Duration {
    return fold(Duration.ZERO) { total, service -> total + service.duration }
}

internal fun List<ServiceSnapshot>.totalPrice(): Money {
    return map { it.price }.reduce { total, price -> total + price }
}

private fun List<ServiceSnapshot>.booked(): List<BookedService> {
    return groupBy { it.id }.values.map { units ->
        BookedService(units.first().name, units.size, units.first().price * units.size)
    }
}

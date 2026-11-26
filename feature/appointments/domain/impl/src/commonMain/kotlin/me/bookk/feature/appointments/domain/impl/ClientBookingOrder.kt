package me.bookk.feature.appointments.domain.impl

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.ClientBooking
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.datasource.DateBounds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

internal fun Appointment.startsAt(): Instant {
    return date.toInstant(TimeZone.currentSystemDefault())
}

internal fun ClientBooking.startsAt(): Instant {
    return when (this) {
        is ClientBooking.Visit -> appointment.startsAt()
        is ClientBooking.Request -> request.date
    }
}

internal fun ClientBookingPeriod.listOrder(): Comparator<ClientBooking> {
    val soonestFirst = compareBy<ClientBooking> { it.startsAt() }
    return when (this) {
        ClientBookingPeriod.UPCOMING -> soonestFirst
        ClientBookingPeriod.PAST -> soonestFirst.reversed()
    }
}

internal fun ClientBookingPeriod.listsAfter(date: Instant, other: Instant): Boolean {
    return when (this) {
        ClientBookingPeriod.UPCOMING -> date > other
        ClientBookingPeriod.PAST -> date < other
    }
}

internal fun ClientBookingPeriod.firstListed(dates: List<Instant>): Instant? {
    return when (this) {
        ClientBookingPeriod.UPCOMING -> dates.minOrNull()
        ClientBookingPeriod.PAST -> dates.maxOrNull()
    }
}

internal fun ClientBookingPeriod.datesListedBetween(after: Instant?, until: Instant?): DateBounds {
    return when (this) {
        ClientBookingPeriod.UPCOMING -> DateBounds(from = after?.plus(1.milliseconds), to = until)
        ClientBookingPeriod.PAST -> DateBounds(from = until, to = after?.minus(1.milliseconds))
    }
}

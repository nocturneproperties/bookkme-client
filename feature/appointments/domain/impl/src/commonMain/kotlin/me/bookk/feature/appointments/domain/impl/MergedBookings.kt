package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.datasource.DateBounds
import kotlin.time.Instant

internal class MergedBookings(
    period: ClientBookingPeriod,
    private val coveredUntil: Instant?,
    private val appointments: FetchedList<Appointment>,
    private val requests: FetchedList<AppointmentRequest>,
) {
    private val boundary = period.firstListed(
        listOfNotNull(appointments.lastDateWithMore, requests.lastDateWithMore),
    )

    private val isWithin = { date: Instant ->
        boundary == null || !period.listsAfter(date, boundary)
    }

    val lists = BookingLists(appointments.itemsWithin(isWithin), requests.itemsWithin(isWithin))

    val coveredDates: DateBounds = period.datesListedBetween(after = coveredUntil, until = boundary)

    fun nextCursor(): String? {
        val next = ClientBookingsCursor(
            appointments = appointments.nextPosition(lists.appointments.size),
            requests = requests.nextPosition(lists.requests.size),
            coveredUntil = boundary,
        )
        return next.takeUnless { it.isAtEnd }?.encode()
    }
}

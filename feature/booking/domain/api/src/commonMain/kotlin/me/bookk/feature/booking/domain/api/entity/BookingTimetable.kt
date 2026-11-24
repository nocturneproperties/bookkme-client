package me.bookk.feature.booking.domain.api.entity

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

data class BookingTimetable(
    val businessName: String,
    val timeZone: TimeZone,
    val specialist: BookingSpecialist?,
    val closedWeekdays: Set<DayOfWeek> = emptySet(),
    val daysOff: List<ClosedRange<LocalDate>> = emptyList(),
) {
    fun isClosedOn(date: LocalDate): Boolean {
        return date.dayOfWeek in closedWeekdays || daysOff.any { date in it }
    }
}

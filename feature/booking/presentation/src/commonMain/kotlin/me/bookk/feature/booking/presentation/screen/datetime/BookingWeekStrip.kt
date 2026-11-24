package me.bookk.feature.booking.presentation.screen.datetime

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.startOfWeek
import me.bookk.feature.booking.domain.api.entity.BookingDay

internal class BookingWeekStrip(
    dateLocalizer: DateLocalizer,
) {

    private val weekdayFormat = dateLocalizer.strict(WEEKDAY_PATTERN, respectUserSettings = true)
    private val dayOfMonthFormat = dateLocalizer.strict(
        DAY_OF_MONTH_PATTERN,
        respectUserSettings = true,
    )
    private val monthFormat = dateLocalizer.strict(MONTH_PATTERN, respectUserSettings = true)

    fun monthTitle(date: LocalDate): StringDesc {
        return monthFormat.format(date).desc()
    }

    fun days(
        picked: LocalDate,
        today: LocalDate,
        week: Map<LocalDate, BookingDay>,
        onDayClick: (LocalDate) -> Unit,
    ): List<BookingDayItem> {
        return picked.weekDates().map { date ->
            dayItem(date, today, isPicked = date == picked, week[date], onDayClick)
        }
    }

    private fun dayItem(
        date: LocalDate,
        today: LocalDate,
        isPicked: Boolean,
        day: BookingDay?,
        onDayClick: (LocalDate) -> Unit,
    ): BookingDayItem {
        val isPast = date < today
        val isClosed = !isPast && day?.isBusinessClosed == true
        return BookingDayItem(
            id = date.toString(),
            weekday = weekday(date, today),
            dayOfMonth = dayOfMonthFormat.format(date).desc(),
            isToday = date == today,
            isSelected = isPicked,
            isEnabled = !isPast && !isClosed,
            isClosed = isClosed,
            onClick = { onDayClick(date) },
        )
    }

    private fun weekday(date: LocalDate, today: LocalDate): StringDesc {
        return when (date == today) {
            true -> BookingRes.strings.booking_date_time_today.desc()
            false -> weekdayFormat.format(date).desc()
        }
    }

    private companion object {
        const val WEEKDAY_PATTERN = "EEE"
        const val DAY_OF_MONTH_PATTERN = "d"
        const val MONTH_PATTERN = "LLLL yyyy"
    }
}

internal fun LocalDate.weekDates(): List<LocalDate> {
    val weekStart = startOfWeek()
    return (0 until DAYS_IN_WEEK).map { weekStart.plus(it, DateTimeUnit.DAY) }
}

internal fun Map<LocalDate, BookingDay>.firstFreeDayFrom(date: LocalDate): LocalDate? {
    return values
        .filter { it.date >= date && it.slots.isNotEmpty() }
        .minOfOrNull { it.date }
}

private const val DAYS_IN_WEEK = 7

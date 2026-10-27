package me.bookk.core.presentation.date

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

expect fun LocalDate.startOfWeek(): LocalDate

fun LocalDate.Companion.atNextWeekDay(dayOfWeek: DayOfWeek): LocalDate {
    val now = LocalDate.today()
    return now.plus(dayOfWeek.isoDayNumber - now.dayOfWeek.isoDayNumber, DateTimeUnit.DAY)
}

fun LocalDate.Companion.today(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return Clock.System.todayIn(timeZone)
}

fun LocalDate.startOfMonth(): LocalDate {
    return LocalDate(year, month, 1)
}

fun LocalDate.toMillis(): Long {
    return atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

fun LocalDate.toUTCMillis(): Long {
    return atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}
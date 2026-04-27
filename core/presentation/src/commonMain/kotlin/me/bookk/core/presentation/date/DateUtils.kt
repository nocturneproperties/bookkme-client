package me.bookk.core.presentation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import kotlin.time.Clock

expect fun LocalDate.startOfWeek(): LocalDate

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
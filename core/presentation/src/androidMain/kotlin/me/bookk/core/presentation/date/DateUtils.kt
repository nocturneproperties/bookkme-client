package me.bookk.core.presentation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

fun LocalDate.toMillis(): Long {
    return atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

fun LocalDate.toUTCMillis(): Long {
    return atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}
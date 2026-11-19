package me.bookk.core.presentation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlin.js.Date

internal fun LocalDateTime.jsDate(): Date {
    return Date(year, month.ordinal, day, hour, minute, second, nanosecond / 1_000_000)
}

internal fun LocalDate.jsDate(): Date {
    return atTime(0, 0).jsDate()
}

internal fun LocalTime.jsDate(): Date {
    return LocalDate.today().atTime(this).jsDate()
}

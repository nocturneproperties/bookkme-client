package me.bookk.core

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

fun LocalDate.Companion.now(): LocalDate {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun LocalTime.Companion.now(): LocalTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
}

fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDate?.orNow(): LocalDate {
    return this ?: LocalDate.now()
}

fun LocalTime?.orNow(): LocalTime {
    return this ?: LocalTime.now()
}

fun LocalDateTime?.orNow(): LocalDateTime {
    return this ?: LocalDateTime.now()
}
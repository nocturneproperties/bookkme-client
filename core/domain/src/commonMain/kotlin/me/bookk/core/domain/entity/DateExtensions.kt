package me.bookk.core.domain.entity

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDateTime.atTime(time: LocalTime): LocalDateTime {
    return date.atTime(time)
}

fun LocalDateTime.plusDayIfInPast(): LocalDateTime {
    return if (LocalDateTime.now() > this) {
        date.plus(1, DateTimeUnit.DAY).atTime(time)
    } else {
        this
    }
}

fun LocalDate.Companion.now(): LocalDate {
    val dateTime = LocalDateTime.now()
    return LocalDate(dateTime.year, dateTime.month, dateTime.dayOfMonth)
}

fun LocalDate.atStartOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return atStartOfDayIn(timeZone).toLocalDateTime(timeZone)
}

fun LocalDate.atEndOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return plus(1, DateTimeUnit.DAY)
        .atStartOfDayIn(timeZone)
        .minus(1, DateTimeUnit.MILLISECOND)
        .toLocalDateTime(timeZone)
}

fun LocalDateTime.equalsByMinute(dateTime: LocalDateTime): Boolean {
    return this.date == dateTime.date && this.hour == dateTime.hour && this.minute == dateTime.minute
}

/**
 * DateTimeFormatter for [LocalDate] formatting to the specified pattern: MM-dd-yyyy
 */
val LocalDate.Companion.formatter: DateTimeFormat<LocalDate>
    get() = Format {
        monthNumber()
        char('-')
        dayOfMonth()
        char('-')
        year()
    }

/**
 * DateTimeFormatter for [LocalDateTime] formatting to the specified pattern: MM-dd-yyyy, HH:mm a
 */
val LocalDateTime.Companion.formatter: DateTimeFormat<LocalDateTime>
    get() = Format {
        monthNumber()
        char('-')
        dayOfMonth()
        char('-')
        year()
        chars(", ")
        amPmHour(padding = Padding.NONE)
        char(':')
        minute()
        char(' ')
        amPmMarker("AM", "PM")
    }

/**
 * TimeFormatter for [LocalTime] to retrieve time in the specified format: HH:mm a
 */
val LocalTime.Companion.formatter: DateTimeFormat<LocalTime>
    get() = Format {
        amPmHour()
        char(':')
        minute()
        char(' ')
        amPmMarker("AM", "PM")
    }
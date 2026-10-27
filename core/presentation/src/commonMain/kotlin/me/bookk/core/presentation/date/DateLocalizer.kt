package me.bookk.core.presentation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

interface DateLocalizer {

    interface Formatter {
        fun format(date: LocalDateTime, relative: Boolean = false): String
        fun format(date: LocalDate, relative: Boolean = false): String
        fun format(time: LocalTime): String
    }

    fun forStyle(dateStyle: DateStyle): Formatter
    fun strict(pattern: String, respectUserSettings: Boolean = false): Formatter
}

expect enum class DateStyle {
    SHORT,
    MEDIUM,
    LONG,
    D_MMM_YYYY_RELATIVE,
    NARROW_WEEKDAY,
    FULL_WEEKDAY
}
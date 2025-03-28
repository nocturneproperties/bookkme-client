package me.bookk.core.presentation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

interface DateLocalizer {
    fun format(date: LocalDateTime, dateStyle: Style, timeStyle: Style): String
    fun format(date: LocalDate, style: Style): String
    fun format(time: LocalTime, style: Style): String

    enum class Style {
        SHORT,
        MEDIUM,
        LONG
    }
}
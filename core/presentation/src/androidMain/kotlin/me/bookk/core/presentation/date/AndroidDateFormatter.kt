package me.bookk.core.presentation.date

import android.icu.text.RelativeDateTimeFormatter
import android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit
import android.icu.text.RelativeDateTimeFormatter.Direction
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toJavaLocalTime
import me.bookk.core.capitalizeChar

internal class AndroidDateFormatter(
    val formatters: AndroidFormatters
) : DateLocalizer.Formatter {

    private val relativeFormatter = RelativeDateTimeFormatter.getInstance()

    override fun format(date: LocalDateTime, relative: Boolean): String {
        if (!relative) return formatters.dateTime.format(date.toJavaLocalDateTime()).orEmpty()
        val today = LocalDate.today()
        val daysTillToday = date.date.daysUntil(today)
        val sameYear = date.year == today.year
        return when {
            daysTillToday == 0 -> relativeFormatter.format(Direction.THIS, AbsoluteUnit.DAY).capitalizeChar()
            daysTillToday == 1 -> relativeFormatter.format(Direction.LAST, AbsoluteUnit.DAY).capitalizeChar()
            sameYear -> formatters.dateTimeSameYear.format(date.toJavaLocalDateTime()).orEmpty()
            else -> formatters.dateTime.format(date.toJavaLocalDateTime()).orEmpty()
        }
    }

    override fun format(date: LocalDate, relative: Boolean): String {
        if (!relative) return formatters.date.format(date.toJavaLocalDate()).orEmpty()

        val today = LocalDate.today()
        val daysTillToday = date.daysUntil(today)
        val sameYear = date.year == today.year
        return when {
            daysTillToday == 0 -> relativeFormatter.format(Direction.THIS, AbsoluteUnit.DAY).capitalizeChar()
            daysTillToday == 1 -> relativeFormatter.format(Direction.LAST, AbsoluteUnit.DAY).capitalizeChar()
            sameYear -> formatters.dateSameYear.format(date.toJavaLocalDate()).orEmpty()
            else -> formatters.date.format(date.toJavaLocalDate()).orEmpty()
        }
    }

    override fun format(time: LocalTime): String {
        return formatters.time.format(time.toJavaLocalTime()).orEmpty()
    }
}
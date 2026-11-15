package me.bookk.core.presentation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(FormatStringsInDatetimeFormats::class)
internal class JsStrictDateFormatter(
    pattern: String,
) : DateLocalizer.Formatter {

    private val format = LocalDateTime.Format { byUnicodePattern(pattern) }

    override fun format(date: LocalDateTime, relative: Boolean): String {
        return format.format(date)
    }

    override fun format(date: LocalDate, relative: Boolean): String {
        return format.format(date.atTime(0, 0))
    }

    override fun format(time: LocalTime): String {
        return format.format(LocalDate.today().atTime(time))
    }
}

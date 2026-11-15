package me.bookk.core.presentation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil
import me.bookk.core.capitalizeChar
import me.bookk.core.presentation.intl.Intl
import me.bookk.core.presentation.intl.intlLocales
import me.bookk.core.presentation.intl.jsOptions
import kotlin.js.Date

internal class JsDateFormatter(
    private val dateStyle: DateStyle,
) : DateLocalizer.Formatter {

    override fun format(date: LocalDateTime, relative: Boolean): String {
        if (!relative) return dateStyle.dateTime.format(date.jsDate())
        val today = LocalDate.today()
        return relativeDay(date.date.daysUntil(today)) ?: when (date.year == today.year) {
            true -> dateStyle.sameYearDateTime.format(date.jsDate())
            false -> dateStyle.dateTime.format(date.jsDate())
        }
    }

    override fun format(date: LocalDate, relative: Boolean): String {
        if (!relative) return dateStyle.date.format(date.jsDate())
        val today = LocalDate.today()
        return relativeDay(date.daysUntil(today)) ?: when (date.year == today.year) {
            true -> dateStyle.sameYearDate.format(date.jsDate())
            false -> dateStyle.date.format(date.jsDate())
        }
    }

    override fun format(time: LocalTime): String {
        return dateStyle.time.format(time.jsDate())
    }

    private fun relativeDay(daysTillToday: Int): String? {
        if (daysTillToday != 0 && daysTillToday != 1) return null
        val formatter = Intl.RelativeTimeFormat(intlLocales(), jsOptions("numeric" to "auto"))
        return formatter.format(-daysTillToday, "day").capitalizeChar()
    }

    private fun IntlPattern.format(date: Date): String {
        return Intl.DateTimeFormat(intlLocales(), options()).format(date)
    }
}

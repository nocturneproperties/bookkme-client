package me.bookk.core.presentation.date

import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil
import me.bookk.android.core.presentation.CorePresentation

internal class IOSDateFormatter(
    val formatters: IOSFormatters
) : DateLocalizer.Formatter {

    private val relativeToday = CorePresentation.strings.date_localizer_today.desc().localized()
    private val relativeYesterday = CorePresentation.strings.date_localizer_yesterday.desc().localized()

    override fun format(date: LocalDateTime, relative: Boolean): String {
        if (!relative) return formatters.dateTime.stringFromDate(date.nsDate())
        val today = LocalDate.today()
        val daysTillToday = date.date.daysUntil(today)
        val sameYear = date.year == today.year
        return when {
            daysTillToday == 0 -> relativeToday
            daysTillToday == 1 -> relativeYesterday
            sameYear -> formatters.dateTimeSameYear.stringFromDate(date.nsDate())
            else -> formatters.dateTime.stringFromDate(date.nsDate())
        }
    }

    override fun format(date: LocalDate, relative: Boolean): String {
        if (!relative) return formatters.date.stringFromDate(date.nsDate())
        val today = LocalDate.today()
        val daysTillToday = date.daysUntil(today)
        val sameYear = date.year == today.year
        return when {
            daysTillToday == 0 -> relativeToday
            daysTillToday == 1 -> relativeYesterday
            sameYear -> formatters.dateSameYear.stringFromDate(date.nsDate())
            else -> formatters.date.stringFromDate(date.nsDate())
        }
    }

    override fun format(time: LocalTime): String {
        return formatters.time.stringFromDate(time.nsDate())
    }
}
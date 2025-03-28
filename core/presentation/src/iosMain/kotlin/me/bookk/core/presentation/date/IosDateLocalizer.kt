package me.bookk.core.presentation.date

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toNSDate
import me.bookk.core.presentation.date.DateLocalizer.Style
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterLongStyle
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSDateFormatterStyle

class IosDateLocalizer : DateLocalizer {

    private val format = NSDateFormatter()

    override fun format(date: LocalDateTime, dateStyle: Style, timeStyle: Style): String {
        format.dateStyle = dateStyle.toNSStyle()
        format.timeStyle = timeStyle.toNSStyle()
        val nsDate = date.toInstant(TimeZone.currentSystemDefault()).toNSDate()
        return format.stringFromDate(nsDate)
    }

    override fun format(date: LocalDate, style: Style): String {
        format.dateStyle = style.toNSStyle()
        format.timeStyle = NSDateFormatterNoStyle
        val nsDate = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toNSDate()
        return format.stringFromDate(nsDate)
    }

    override fun format(time: LocalTime, style: Style): String {
        format.dateStyle = NSDateFormatterNoStyle
        format.timeStyle = style.toNSStyle()
        val nsDate = Instant.fromEpochMilliseconds(time.toMillisecondOfDay().toLong()).toNSDate()
        return format.stringFromDate(nsDate)
    }

    private fun Style.toNSStyle(): NSDateFormatterStyle {
        return when (this) {
            Style.SHORT -> NSDateFormatterShortStyle
            Style.MEDIUM -> NSDateFormatterMediumStyle
            Style.LONG -> NSDateFormatterLongStyle
        }
    }
}
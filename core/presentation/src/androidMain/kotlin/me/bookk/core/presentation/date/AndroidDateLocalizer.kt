package me.bookk.core.presentation.date

import android.content.Context
import android.text.format.DateFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaInstant
import me.bookk.core.presentation.date.DateLocalizer.Style
import java.time.Instant
import java.util.Date

class AndroidDateLocalizer(context: Context) : DateLocalizer {

    private val short = DateFormat.getDateFormat(context)
    private val medium = DateFormat.getMediumDateFormat(context)
    private val long = DateFormat.getLongDateFormat(context)

    private val timeFormat = DateFormat.getTimeFormat(context)

    override fun format(date: LocalDateTime, dateStyle: Style, timeStyle: Style): String {
        return "${format(date.date, dateStyle)} ${format(date.time, timeStyle)}"
    }

    override fun format(date: LocalDate, style: Style): String {
        val jvmInstant = date.atStartOfDayIn(TimeZone.currentSystemDefault())
        val legacyDate = Date.from(jvmInstant.toJavaInstant())
        return when (style) {
            Style.SHORT -> short.format(legacyDate)
            Style.MEDIUM -> medium.format(legacyDate)
            Style.LONG -> long.format(legacyDate)
        }
    }

    override fun format(time: LocalTime, style: Style): String {
        val legacyDate = Date.from(Instant.ofEpochMilli(time.toMillisecondOfDay().toLong()))
        return timeFormat.format(legacyDate)
    }
}
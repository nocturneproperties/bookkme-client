package me.bookk.core.presentation.date

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toNSDate
import me.bookk.core.UsedInSwift
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterStyle
import platform.Foundation.NSLocale
import platform.Foundation.autoupdatingCurrentLocale

actual fun LocalDate.startOfWeek(): LocalDate {
    val calendar = NSCalendar.currentCalendar
    val firstWeekday = calendar.firstWeekday.toInt() // 1 = Sunday, 2 = Monday 3
    // Convert to kotlinx DayOfWeek (Monday = 1) isoDayNumber
    val firstDayIso = if (firstWeekday == 1) 7 else firstWeekday - 1
    val currentIso = dayOfWeek.isoDayNumber

    val daysToSubtract = (currentIso - firstDayIso + 7) % 7

    return this.minus(daysToSubtract, DateTimeUnit.DAY)
}

internal fun LocalDateTime.nsDate(): NSDate {
    return toInstant(TimeZone.currentSystemDefault()).toNSDate()
}

internal fun LocalDate.nsDate(): NSDate {
    return atStartOfDayIn(TimeZone.currentSystemDefault()).toNSDate()
}

internal fun LocalTime.nsDate(): NSDate {
    return LocalDate.today().atTime(this).nsDate()
}

fun NSDateFormatter.matchToUserPreferences(pattern: String) {
    val skeleton = pattern.filter { it.isLetter() }
    setLocalizedDateFormatFromTemplate(skeleton)
}

fun NSDateFormatter.matchToUserPreferences(
    dateStyle: NSDateFormatterStyle = NSDateFormatterNoStyle,
    timeStyle: NSDateFormatterStyle = NSDateFormatterNoStyle
) {
    val probe = NSDateFormatter().apply {
        locale = NSLocale.autoupdatingCurrentLocale
        this.dateStyle = dateStyle
        this.timeStyle = timeStyle
    }
    matchToUserPreferences(probe.dateFormat)
}

fun NSDate.asLocalDate(timeZone: TimeZone): LocalDate {
    return toKotlinInstant()
        .toLocalDateTime(timeZone)
        .date
}

@UsedInSwift
fun NSDate.asLocalDate(): LocalDate {
    return toKotlinInstant()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
}
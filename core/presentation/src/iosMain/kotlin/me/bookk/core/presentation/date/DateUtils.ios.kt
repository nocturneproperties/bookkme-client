package me.bookk.core.presentation.date

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toNSDate
import me.bookk.core.UsedInSwift
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun LocalDate.startOfWeek(): LocalDate {
    val calendar = NSCalendar.currentCalendar
    val firstWeekday = calendar.firstWeekday.toInt() // 1 = Sunday, 2 = Monday, etc.

    // Convert to kotlinx DayOfWeek (Monday = 0)
    val firstDayOrdinal = if (firstWeekday == 1) 6 else firstWeekday - 2

    val currentOrdinal = this.dayOfWeek.ordinal
    val daysToSubtract = (currentOrdinal - firstDayOrdinal + 7) % 7

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
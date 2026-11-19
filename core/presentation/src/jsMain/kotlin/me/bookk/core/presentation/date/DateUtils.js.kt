package me.bookk.core.presentation.date

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import me.bookk.core.presentation.intl.Intl
import me.bookk.core.presentation.intl.resolvedLocaleTag

actual fun LocalDate.startOfWeek(): LocalDate {
    val daysToSubtract = (dayOfWeek.isoDayNumber - firstIsoDayOfWeek() + 7) % 7
    return minus(daysToSubtract, DateTimeUnit.DAY)
}

private fun firstIsoDayOfWeek(): Int {
    val locale = Intl.Locale(resolvedLocaleTag()).asDynamic()
    val weekInfo = if (locale.getWeekInfo != undefined) locale.getWeekInfo() else locale.weekInfo
    return (weekInfo?.firstDay as? Int) ?: 1
}

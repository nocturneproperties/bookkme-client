package me.bookk.core.presentation.date

import java.time.format.FormatStyle

actual enum class DateStyle(
    val datePattern: String,
    val dateTimePattern: String,
    val sameYearDateTimePattern: String,
    val sameYearDatePattern: String,
    val timePattern: String
) {
    SHORT(
        FormatStyle.SHORT.getDatePattern(),
        FormatStyle.SHORT.getDateTimePattern(),
        FormatStyle.SHORT.getDatePattern(),
        FormatStyle.SHORT.getDateTimePattern(),
        FormatStyle.SHORT.getTimePattern(),
    ),
    MEDIUM(
        FormatStyle.MEDIUM.getDatePattern(),
        FormatStyle.MEDIUM.getDateTimePattern(),
        FormatStyle.MEDIUM.getDatePattern(),
        FormatStyle.MEDIUM.getDateTimePattern(),
        FormatStyle.MEDIUM.getTimePattern(),
    ),
    LONG(
        FormatStyle.LONG.getDatePattern(),
        FormatStyle.LONG.getDateTimePattern(),
        FormatStyle.LONG.getDatePattern(),
        FormatStyle.LONG.getDateTimePattern(),
        FormatStyle.LONG.getTimePattern(),
    ),
    D_MMM_YYYY_RELATIVE(
        datePattern = "d MMM yyyy",
        dateTimePattern = "d MMM yyyy HH:mm",
        sameYearDatePattern = "d MMM",
        sameYearDateTimePattern = "d MMM yyyy HH:mm",
        timePattern = "HH:mm"
    ),
    NARROW_WEEKDAY(
        datePattern = "EEEEE",
        dateTimePattern = "EEEEE",
        sameYearDatePattern = "EEEEE",
        sameYearDateTimePattern = "EEEEE HH:mm",
        timePattern = "HH:mm"
    ),
    FULL_WEEKDAY(
        datePattern = "EEEE",
        dateTimePattern = "EEEE",
        sameYearDatePattern = "EEEE",
        sameYearDateTimePattern = "EEEE HH:mm",
        timePattern = "HH:mm"
    )
}
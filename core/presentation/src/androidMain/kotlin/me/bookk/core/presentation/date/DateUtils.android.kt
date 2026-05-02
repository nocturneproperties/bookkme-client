package me.bookk.core.presentation.date

import android.text.format.DateFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale


actual fun LocalDate.startOfWeek(): LocalDate {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    return toJavaLocalDate()
        .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        .toKotlinLocalDate()
}

fun String.matchToUserPreferences(): String {
    val skeleton = filter { it.isLetter() }
    return DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton)
}

fun FormatStyle.getDatePattern(): String {
    return DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        this,
        null,
        Chronology.ofLocale(Locale.getDefault()),
        Locale.getDefault()
    )
}

fun FormatStyle.getDateTimePattern(): String {
    return DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        this,
        this,
        Chronology.ofLocale(Locale.getDefault()),
        Locale.getDefault()
    )
}

fun FormatStyle.getTimePattern(): String {
    return DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        null,
        this,
        Chronology.ofLocale(Locale.getDefault()),
        Locale.getDefault()
    )
}
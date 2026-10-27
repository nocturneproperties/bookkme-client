package me.bookk.core.presentation.date

import me.bookk.core.android.AndroidActivityAware
import java.time.format.DateTimeFormatter

internal class AndroidDateLocalizer : AndroidActivityAware(), DateLocalizer {

    private fun DateStyle.nativeFormat(): AndroidFormatters {
        return AndroidFormatters(
            dateTime = DateTimeFormatter.ofPattern(dateTimePattern.matchToUserPreferences()),
            dateTimeSameYear = DateTimeFormatter.ofPattern(sameYearDateTimePattern.matchToUserPreferences()),
            date = DateTimeFormatter.ofPattern(datePattern.matchToUserPreferences()),
            dateSameYear = DateTimeFormatter.ofPattern(sameYearDatePattern.matchToUserPreferences()),
            time = DateTimeFormatter.ofPattern(timePattern.matchToUserPreferences())
        )
    }

    override fun forStyle(dateStyle: DateStyle): DateLocalizer.Formatter {
        return AndroidDateFormatter(dateStyle.nativeFormat())
    }

    override fun strict(pattern: String, respectUserSettings: Boolean): DateLocalizer.Formatter {
        val changedPattern = if (respectUserSettings) pattern.matchToUserPreferences() else pattern
        val formatter = DateTimeFormatter.ofPattern(changedPattern)
        return AndroidDateFormatter(
            AndroidFormatters(
                dateTime = formatter,
                dateTimeSameYear = formatter,
                date = formatter,
                dateSameYear = formatter,
                time = formatter
            )
        )
    }
}

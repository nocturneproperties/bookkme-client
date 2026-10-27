package me.bookk.core.presentation.date

import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.autoupdatingCurrentLocale
import platform.Foundation.localTimeZone

class IosDateLocalizer: DateLocalizer {

    private fun DateStyle.nativeFormat(): IOSFormatters {
        val dateFormatter = NSDateFormatter().apply {
            locale = NSLocale.autoupdatingCurrentLocale
            timeZone = NSTimeZone.localTimeZone
            dateModifier()
        }
        val sameYearDateFormatter = NSDateFormatter().apply {
            locale = NSLocale.autoupdatingCurrentLocale
            timeZone = NSTimeZone.localTimeZone
            sameYearDateModifier()
        }
        val dateTimeFormatter = NSDateFormatter().apply {
            locale = NSLocale.autoupdatingCurrentLocale
            timeZone = NSTimeZone.localTimeZone
            dateTimeModifier()
        }
        val sameYearDateTimeFormatter = NSDateFormatter().apply {
            locale = NSLocale.autoupdatingCurrentLocale
            timeZone = NSTimeZone.localTimeZone
            sameYearDateTimeModifier()
        }
        val timeFormatter = NSDateFormatter().apply {
            locale = NSLocale.autoupdatingCurrentLocale
            timeZone = NSTimeZone.localTimeZone
            timeModifier()
        }
        return IOSFormatters(
            date = dateFormatter,
            dateTime = dateTimeFormatter,
            dateSameYear = sameYearDateFormatter,
            dateTimeSameYear = sameYearDateTimeFormatter,
            time = timeFormatter
        )
    }

    override fun forStyle(dateStyle: DateStyle): DateLocalizer.Formatter {
        return IOSDateFormatter(dateStyle.nativeFormat())
    }

    override fun strict(pattern: String, respectUserSettings: Boolean): DateLocalizer.Formatter {
        val formatter = NSDateFormatter().apply {
            locale = NSLocale(localeIdentifier = "en_US_POSIX")
            timeZone = NSTimeZone.localTimeZone
            if (respectUserSettings) {
                matchToUserPreferences(pattern)
            } else {
                dateFormat = pattern
            }
        }
        return IOSDateFormatter(IOSFormatters(formatter, formatter, formatter, formatter, formatter))
    }
}
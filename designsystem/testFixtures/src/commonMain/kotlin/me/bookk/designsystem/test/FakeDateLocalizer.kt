package me.bookk.designsystem.test

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle

class FakeDateLocalizer : DateLocalizer {
    override fun forStyle(dateStyle: DateStyle): DateLocalizer.Formatter {
        return FakeFormatter
    }

    override fun strict(pattern: String, respectUserSettings: Boolean): DateLocalizer.Formatter {
        return FakeFormatter
    }

    object FakeFormatter : DateLocalizer.Formatter {
        override fun format(date: LocalDateTime, relative: Boolean): String {
            return date.toString()
        }

        override fun format(date: LocalDate, relative: Boolean): String {
            return date.toString()
        }

        override fun format(time: LocalTime): String {
            return time.toString()
        }
    }
}

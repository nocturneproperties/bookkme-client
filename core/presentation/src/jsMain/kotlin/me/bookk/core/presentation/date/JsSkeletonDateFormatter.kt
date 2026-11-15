package me.bookk.core.presentation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import me.bookk.core.presentation.intl.Intl
import me.bookk.core.presentation.intl.intlLocales

internal class JsSkeletonDateFormatter(
    private val skeleton: IntlPattern.Skeleton,
) : DateLocalizer.Formatter {

    override fun format(date: LocalDateTime, relative: Boolean): String {
        return Intl.DateTimeFormat(intlLocales(), skeleton.options()).format(date.jsDate())
    }

    override fun format(date: LocalDate, relative: Boolean): String {
        return Intl.DateTimeFormat(intlLocales(), skeleton.options()).format(date.jsDate())
    }

    override fun format(time: LocalTime): String {
        return Intl.DateTimeFormat(intlLocales(), skeleton.options()).format(time.jsDate())
    }
}

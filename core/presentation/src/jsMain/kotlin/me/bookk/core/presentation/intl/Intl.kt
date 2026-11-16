package me.bookk.core.presentation.intl

import kotlin.js.Date

internal external object Intl {
    class DateTimeFormat(
        locales: Array<String>,
        options: dynamic,
    ) {
        fun format(date: Date): String
    }

    class RelativeTimeFormat(
        locales: Array<String>,
        options: dynamic,
    ) {
        fun format(value: Int, unit: String): String
    }

    class Collator(
        locales: Array<String>,
        options: dynamic,
    ) {
        fun compare(first: String, second: String): Int
    }

    class Locale(
        tag: String,
    )
}

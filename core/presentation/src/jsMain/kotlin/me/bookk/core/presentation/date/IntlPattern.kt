package me.bookk.core.presentation.date

import me.bookk.core.presentation.intl.jsOptions

internal sealed interface IntlPattern {
    fun options(): dynamic

    class Styles(
        private val dateStyle: String? = null,
        private val timeStyle: String? = null,
    ) : IntlPattern {
        override fun options(): dynamic {
            return jsOptions(
                *listOfNotNull(
                    dateStyle?.let { "dateStyle" to it },
                    timeStyle?.let { "timeStyle" to it },
                    timeStyle?.let { "hourCycle" to "h23" },
                ).toTypedArray(),
            )
        }
    }

    class Skeleton(
        private val pattern: String,
    ) : IntlPattern {
        override fun options(): dynamic {
            return jsOptions(*fields(pattern).toTypedArray())
        }
    }
}

private fun fields(pattern: String): List<Pair<String, Any>> {
    return letterRuns(pattern).flatMap { (letter, count) -> field(letter, count) }
}

private fun letterRuns(pattern: String): List<Pair<Char, Int>> {
    return Regex("([A-Za-z])\\1*").findAll(pattern).map {
        it.value.first() to it.value.length
    }.toList()
}

private fun field(letter: Char, count: Int): List<Pair<String, Any>> {
    return when (letter) {
        'y' -> listOf("year" to if (count == 2) "2-digit" else "numeric")
        'M', 'L' -> listOf("month" to monthStyle(count))
        'd' -> listOf("day" to if (count == 2) "2-digit" else "numeric")
        'E' -> listOf("weekday" to weekdayStyle(count))
        'H' -> listOf("hour" to "2-digit", "hourCycle" to "h23")
        'h' -> listOf("hour" to "numeric", "hourCycle" to "h12")
        'm' -> listOf("minute" to "2-digit")
        's' -> listOf("second" to "2-digit")
        else -> emptyList()
    }
}

private fun monthStyle(count: Int): String {
    return when (count) {
        1 -> "numeric"
        2 -> "2-digit"
        3 -> "short"
        4 -> "long"
        else -> "narrow"
    }
}

private fun weekdayStyle(count: Int): String {
    return when (count) {
        4 -> "long"
        5 -> "narrow"
        else -> "short"
    }
}

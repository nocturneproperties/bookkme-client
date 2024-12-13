package me.bookk.core.presentation.date

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
class SelectableDateRange(
    val max: LocalDate?,
    val min: LocalDate?,
) : SelectableDates {

    init {
        require(min == null || max == null || min <= max) {
            "Invalid date range: min must be before or equal to max"
        }
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val selectedDate = Instant.fromEpochMilliseconds(utcTimeMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return min?.let { selectedDate.date >= it } ?: true
                && max?.let { selectedDate.date <= it } ?: true
    }

    override fun isSelectableYear(year: Int): Boolean {
        return min?.let { year >= it.year } ?: true
                && max?.let { year <= it.year } ?: true
    }
}
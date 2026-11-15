package me.bookk.database.converter

import androidx.room3.ColumnTypeConverter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class DurationConverter {
    @ColumnTypeConverter
    fun longToDuration(long: Long?): Duration? {
        return long?.milliseconds
    }

    @ColumnTypeConverter
    fun durationToLong(duration: Duration?): Long? {
        return duration?.inWholeMilliseconds
    }
}

package me.bookk.database.converter

import androidx.room.TypeConverter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class DurationConverter {
    @TypeConverter
    fun longToDuration(long: Long?): Duration? {
        return long?.milliseconds
    }

    @TypeConverter
    fun durationToLong(duration: Duration?): Long? {
        return duration?.inWholeMilliseconds
    }
}
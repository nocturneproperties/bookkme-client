package me.bookk.database.converter

import androidx.room.TypeConverter
import kotlin.time.Instant

internal class InstantConverter {
    @TypeConverter
    fun longToInstant(long: Long?): Instant? {
        return long?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }
}
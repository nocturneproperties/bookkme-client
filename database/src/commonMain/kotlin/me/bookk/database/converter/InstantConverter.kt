package me.bookk.database.converter

import androidx.room3.ColumnTypeConverter
import kotlin.time.Instant

internal class InstantConverter {
    @ColumnTypeConverter
    fun longToInstant(long: Long?): Instant? {
        return long?.let { Instant.fromEpochMilliseconds(it) }
    }

    @ColumnTypeConverter
    fun instantToLong(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }
}

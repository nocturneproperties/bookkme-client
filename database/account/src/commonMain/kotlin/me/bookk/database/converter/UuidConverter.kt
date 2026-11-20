package me.bookk.database.converter

import androidx.room3.ColumnTypeConverter
import kotlin.uuid.Uuid

class UuidConverter {

    @ColumnTypeConverter
    fun stringToUuid(string: String?): Uuid? {
        return string?.let { Uuid.parse(it) }
    }

    @ColumnTypeConverter
    fun uuidToString(uuid: Uuid?): String? {
        return uuid?.toString()
    }
}

package me.bookk.database.converter

import androidx.room.TypeConverter
import kotlin.uuid.Uuid

internal class UuidConverter {

    @TypeConverter
    fun stringToUuid(string: String?): Uuid? {
        return string?.let { Uuid.parse(it) }
    }

    @TypeConverter
    fun uuidToString(uuid: Uuid?): String? {
        return uuid?.toString()
    }
}
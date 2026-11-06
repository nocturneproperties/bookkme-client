package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "personal_data_export")
data class PersonalDataExportEntity(
    @PrimaryKey val id: Int = 0,
    val status: String,
    val requestedAt: Instant?,
    val expiresAt: Instant?,
    val availableAgainAt: Instant?,
)

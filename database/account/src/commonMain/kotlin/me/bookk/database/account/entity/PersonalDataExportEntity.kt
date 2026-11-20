package me.bookk.database.account.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "personal_data_export")
data class PersonalDataExportEntity(
    @PrimaryKey val id: Int = 0,
    val status: String,
    val requestedAt: Instant?,
    val expiresAt: Instant?,
    val availableAgainAt: Instant?,
)

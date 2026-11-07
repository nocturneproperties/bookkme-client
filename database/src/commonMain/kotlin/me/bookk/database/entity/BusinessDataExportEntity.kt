package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(tableName = "business_data_export")
data class BusinessDataExportEntity(
    @PrimaryKey val businessId: Uuid,
    val status: String,
    val requestedAt: Instant?,
    val expiresAt: Instant?,
    val availableAgainAt: Instant?,
)

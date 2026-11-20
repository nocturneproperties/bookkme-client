package me.bookk.database.business.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
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

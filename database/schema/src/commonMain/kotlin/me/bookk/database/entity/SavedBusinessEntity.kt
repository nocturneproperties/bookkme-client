package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(tableName = "saved_business")
data class SavedBusinessEntity(
    @PrimaryKey val businessId: Uuid,
    val enabled: Boolean,
    val savedAt: Instant,
    val lastVisitAt: Instant?,
)

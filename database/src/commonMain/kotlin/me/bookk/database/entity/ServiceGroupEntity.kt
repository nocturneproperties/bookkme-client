package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "service_group",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = CASCADE
        ),
    ],
    indices = [Index("businessId")]
)
class ServiceGroupEntity(
    @PrimaryKey
    val id: Uuid,
    val businessId: Uuid,
    val name: String,
    val createdAt: Instant
)
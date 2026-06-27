package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "service",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = ServiceGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = CASCADE
        ),
    ],
    indices = [
        Index("businessId"),
        Index("groupId"),
    ]
)
class ServiceEntity(
    @PrimaryKey
    val id: Uuid,
    val businessId: Uuid,
    val groupId: Uuid,
    val name: String,
    val duration: Duration,
    val priceCurrency: String,
    val priceValue: Long,
    val isAvailable: Boolean,
    val createdAt: Instant
)
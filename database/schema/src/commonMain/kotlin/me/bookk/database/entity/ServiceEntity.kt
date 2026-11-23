package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
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
            onDelete = CASCADE,
        ),
        ForeignKey(
            entity = ServiceGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [
        Index("businessId"),
        Index("groupId"),
    ],
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
    val createdAt: Instant,
)

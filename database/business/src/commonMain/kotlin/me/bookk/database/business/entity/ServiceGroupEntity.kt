package me.bookk.database.business.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "service_group",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("businessId")],
)
class ServiceGroupEntity(
    @PrimaryKey
    val id: Uuid,
    val businessId: Uuid,
    val name: String,
    val createdAt: Instant,
)

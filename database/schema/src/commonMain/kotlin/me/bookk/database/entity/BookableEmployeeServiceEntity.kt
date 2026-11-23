package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "bookable_employee_service",
    primaryKeys = ["employeeId", "id"],
    foreignKeys = [
        ForeignKey(
            entity = BookableEmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("employeeId")],
)
class BookableEmployeeServiceEntity(
    val employeeId: Uuid,
    val id: Uuid,
    val businessId: Uuid,
    val groupId: Uuid,
    val groupBusinessId: Uuid,
    val groupName: String,
    val groupCreatedAt: Instant,
    val name: String,
    val duration: Duration,
    val priceCurrency: String,
    val priceValue: Long,
    val isAvailable: Boolean,
    val createdAt: Instant,
)

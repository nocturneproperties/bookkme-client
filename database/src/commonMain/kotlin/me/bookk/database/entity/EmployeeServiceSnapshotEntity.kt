package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "employee_service_snapshot",
    primaryKeys = ["employeeId", "id"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("employeeId")]
)
class EmployeeServiceSnapshotEntity(
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
    val createdAt: Instant
)

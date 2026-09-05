package me.bookk.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment",
    indices = [
        Index("businessId")
    ]
)
class AppointmentEntity(
    @PrimaryKey val id: Uuid,
    val userId: Uuid,
    val businessId: Uuid,
    @ColumnInfo(defaultValue = "00000000-0000-0000-0000-000000000000") val employeeId: Uuid,
    @ColumnInfo(defaultValue = "00000000-0000-0000-0000-000000000000") val employeeUserId: Uuid,
    @ColumnInfo(defaultValue = "") val employeeFullName: String,
    val date: Instant,
    val status: String,
    val note: String,
    val cancellationReason: String,
    val clientId: Uuid,
    val clientFullName: String,
    val clientPhone: String?,
    val clientEmail: String?
)

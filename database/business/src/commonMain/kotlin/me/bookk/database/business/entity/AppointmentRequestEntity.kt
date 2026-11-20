package me.bookk.database.business.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_request",
    indices = [Index("businessId")],
)
class AppointmentRequestEntity(
    @PrimaryKey val id: Uuid,
    val userId: Uuid,
    val businessId: Uuid,
    @ColumnInfo(defaultValue = "00000000-0000-0000-0000-000000000000") val employeeId: Uuid,
    @ColumnInfo(defaultValue = "00000000-0000-0000-0000-000000000000") val employeeUserId: Uuid,
    @ColumnInfo(defaultValue = "") val employeeFullName: String,
    val status: String,
    val date: Instant,
    val note: String,
    val declineReason: String,
    val clientId: Uuid,
    val clientFullName: String,
    val clientPhone: String?,
    val clientEmail: String?,
    val clientUserId: Uuid? = null,
)

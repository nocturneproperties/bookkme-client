package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_request",
    indices = [Index("businessId")]
)
class AppointmentRequestEntity(
    @PrimaryKey val id: Uuid,
    val userId: Uuid,
    val businessId: Uuid,
    val status: String,
    val date: Instant,
    val note: String,
    val declineReason: String,
    val clientId: Uuid,
    val clientFullName: String,
    val clientPhone: String,
    val clientEmail: String
)

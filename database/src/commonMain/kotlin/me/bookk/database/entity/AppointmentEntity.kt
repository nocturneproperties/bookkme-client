package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment",
    indices = [
        Index("businessId"),
        Index("localDate")
    ]
)
class AppointmentEntity(
    @PrimaryKey val id: Uuid,
    val userId: Uuid,
    val businessId: Uuid,
    val date: Instant,
    val localDate: String,
    val status: String,
    val note: String,
    val cancellationReason: String,
    val clientId: Uuid,
    val clientFullName: String,
    val clientPhone: String,
    val clientEmail: String
)

package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_adjustment_service",
    primaryKeys = ["appointmentId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("appointmentId")]
)
class AppointmentAdjustmentServiceEntity(
    val appointmentId: Uuid,
    val position: Int,
    val id: Uuid,
    val name: String,
    val groupId: Uuid,
    val priceCurrency: String,
    val priceValue: Long,
    val duration: Duration
)

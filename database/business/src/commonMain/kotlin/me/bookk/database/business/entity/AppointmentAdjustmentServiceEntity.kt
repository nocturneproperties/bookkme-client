package me.bookk.database.business.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
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
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("appointmentId")],
)
class AppointmentAdjustmentServiceEntity(
    val appointmentId: Uuid,
    val position: Int,
    val id: Uuid,
    val name: String,
    val groupId: Uuid,
    val priceCurrency: String,
    val priceValue: Long,
    val duration: Duration,
)

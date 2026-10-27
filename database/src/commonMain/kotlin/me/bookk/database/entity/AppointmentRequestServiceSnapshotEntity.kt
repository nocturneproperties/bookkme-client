package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_request_service_snapshot",
    primaryKeys = ["requestId", "id"],
    foreignKeys = [
        ForeignKey(
            entity = AppointmentRequestEntity::class,
            parentColumns = ["id"],
            childColumns = ["requestId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("requestId")]
)
class AppointmentRequestServiceSnapshotEntity(
    val requestId: Uuid,
    val id: Uuid,
    val name: String,
    val groupId: Uuid,
    val priceCurrency: String,
    val priceValue: Long,
    val duration: Duration
)

package me.bookk.database.business.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
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
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("requestId")],
)
class AppointmentRequestServiceSnapshotEntity(
    val requestId: Uuid,
    val id: Uuid,
    val name: String,
    val groupId: Uuid,
    val priceCurrency: String,
    val priceValue: Long,
    val duration: Duration,
)

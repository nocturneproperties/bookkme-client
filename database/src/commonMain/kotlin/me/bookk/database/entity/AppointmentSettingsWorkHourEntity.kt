package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_settings_working_time",
    primaryKeys = ["settingsId", "dayOfWeek", "from", "to"],
    foreignKeys = [
        ForeignKey(
            entity = AppointmentSettingsEntity::class,
            parentColumns = ["id"],
            childColumns = ["settingsId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("settingsId")]
)
class AppointmentSettingsWorkHourEntity(
    val settingsId: Uuid,
    val dayOfWeek: String,
    val from: String,
    val to: String
)

package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_settings_day_schedule",
    primaryKeys = ["settingsId", "dayOfWeek"],
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
class AppointmentSettingsDayScheduleEntity(
    val settingsId: Uuid,
    val dayOfWeek: String,
    val isActive: Boolean
)

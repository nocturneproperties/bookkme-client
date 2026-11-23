package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_settings_day_schedule",
    primaryKeys = ["settingsId", "dayOfWeek"],
    foreignKeys = [
        ForeignKey(
            entity = AppointmentSettingsEntity::class,
            parentColumns = ["id"],
            childColumns = ["settingsId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("settingsId")],
)
class AppointmentSettingsDayScheduleEntity(
    val settingsId: Uuid,
    val dayOfWeek: String,
    val isActive: Boolean,
)

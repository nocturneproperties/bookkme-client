package me.bookk.database.business.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_settings_day_off",
    primaryKeys = ["settingsId", "start", "end"],
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
class AppointmentSettingsDayOffEntity(
    val settingsId: Uuid,
    val start: String,
    val end: String,
)

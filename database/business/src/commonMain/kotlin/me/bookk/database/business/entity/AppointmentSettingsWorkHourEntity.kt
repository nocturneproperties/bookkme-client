package me.bookk.database.business.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_settings_working_time",
    primaryKeys = ["settingsId", "dayOfWeek", "from", "to"],
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
class AppointmentSettingsWorkHourEntity(
    val settingsId: Uuid,
    val dayOfWeek: String,
    val from: String,
    val to: String,
)

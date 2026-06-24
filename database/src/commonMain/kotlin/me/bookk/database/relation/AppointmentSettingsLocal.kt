package me.bookk.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import me.bookk.database.entity.AppointmentSettingsDayOffEntity
import me.bookk.database.entity.AppointmentSettingsDayScheduleEntity
import me.bookk.database.entity.AppointmentSettingsEntity
import me.bookk.database.entity.AppointmentSettingsWorkHourEntity

class AppointmentSettingsLocal(
    @Embedded val entity: AppointmentSettingsEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "settingsId"
    )
    val daySchedules: List<AppointmentSettingsDayScheduleEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "settingsId"
    )
    val workHours: List<AppointmentSettingsWorkHourEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "settingsId"
    )
    val dayOffs: List<AppointmentSettingsDayOffEntity>
)

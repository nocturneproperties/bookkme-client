package me.bookk.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.entity.AppointmentSettingsDayOffEntity
import me.bookk.database.entity.AppointmentSettingsDayScheduleEntity
import me.bookk.database.entity.AppointmentSettingsEntity
import me.bookk.database.entity.AppointmentSettingsWorkHourEntity

class AppointmentSettingsLocal(
    @Embedded val entity: AppointmentSettingsEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["settingsId"],
    )
    val daySchedules: List<AppointmentSettingsDayScheduleEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["settingsId"],
    )
    val workHours: List<AppointmentSettingsWorkHourEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["settingsId"],
    )
    val dayOffs: List<AppointmentSettingsDayOffEntity>,
)

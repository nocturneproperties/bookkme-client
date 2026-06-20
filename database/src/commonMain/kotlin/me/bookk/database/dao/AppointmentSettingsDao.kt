package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import me.bookk.database.entity.AppointmentSettingsDayOffEntity
import me.bookk.database.entity.AppointmentSettingsDayScheduleEntity
import me.bookk.database.entity.AppointmentSettingsEntity
import me.bookk.database.entity.AppointmentSettingsWorkHourEntity
import me.bookk.database.relation.AppointmentSettingsLocal
import kotlin.uuid.Uuid

@Dao
abstract class AppointmentSettingsDao {

    @Transaction
    @Query("SELECT * FROM appointment_settings WHERE businessId = :businessId")
    abstract suspend fun getByBusinessId(businessId: Uuid): AppointmentSettingsLocal?

    @Upsert
    abstract suspend fun upsertSettings(settings: AppointmentSettingsEntity)

    @Query("DELETE FROM appointment_settings_day_schedule WHERE settingsId = :settingsId")
    abstract suspend fun deleteDaySchedules(settingsId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDaySchedules(daySchedules: List<AppointmentSettingsDayScheduleEntity>)

    @Query("DELETE FROM appointment_settings_working_time WHERE settingsId = :settingsId")
    abstract suspend fun deleteWorkHours(settingsId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertWorkHours(workHours: List<AppointmentSettingsWorkHourEntity>)

    @Query("DELETE FROM appointment_settings_day_off WHERE settingsId = :settingsId")
    abstract suspend fun deleteDayOffs(settingsId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDayOffs(dayOffs: List<AppointmentSettingsDayOffEntity>)

    @Transaction
    open suspend fun upsertWithChildren(
        settings: AppointmentSettingsEntity,
        daySchedules: List<AppointmentSettingsDayScheduleEntity>,
        workHours: List<AppointmentSettingsWorkHourEntity>,
        dayOffs: List<AppointmentSettingsDayOffEntity>
    ) {
        upsertSettings(settings)
        deleteDaySchedules(settings.id)
        insertDaySchedules(daySchedules)
        deleteWorkHours(settings.id)
        insertWorkHours(workHours)
        deleteDayOffs(settings.id)
        insertDayOffs(dayOffs)
    }
}

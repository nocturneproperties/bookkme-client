package me.bookk.feature.appointments.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import kotlin.uuid.Uuid

interface AppointmentSettingsDataSource {
    suspend fun getAppointmentSettings(businessId: Uuid): AppointmentSettings

    suspend fun updateAppointmentSettings(settings: AppointmentSettings): AppointmentSettings

    fun observeAppointmentSettingsDBChanges(businessId: Uuid): Flow<AppointmentSettings?>

    suspend fun saveAppointmentSettingsInDB(settings: AppointmentSettings)
}

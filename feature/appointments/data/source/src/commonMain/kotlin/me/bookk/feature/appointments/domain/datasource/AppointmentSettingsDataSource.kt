package me.bookk.feature.appointments.domain.datasource

import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import kotlin.uuid.Uuid

interface AppointmentSettingsDataSource {
    suspend fun getAppointmentSettings(businessId: Uuid): AppointmentSettings

    suspend fun updateAppointmentSettings(settings: AppointmentSettings): AppointmentSettings

    suspend fun getAppointmentSettingsFromDB(businessId: Uuid): AppointmentSettings?

    suspend fun saveAppointmentSettingsInDB(settings: AppointmentSettings)
}

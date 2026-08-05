package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.UpdateAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.datasource.AppointmentSettingsDataSource

internal class UpdateAppointmentSettingsImpl(
    private val appointmentSettingsDataSource: AppointmentSettingsDataSource
) : UpdateAppointmentSettings {

    override suspend fun invoke(settings: AppointmentSettings): AppointmentSettings {
        val result = appointmentSettingsDataSource.updateAppointmentSettings(settings)
        appointmentSettingsDataSource.saveAppointmentSettingsInDB(result)
        return result
    }
}

package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.datasource.AppointmentSettingsDataSource
import kotlin.uuid.Uuid

internal class GetAppointmentSettingsImpl(
    private val appointmentSettingsDataSource: AppointmentSettingsDataSource
) : GetAppointmentSettings {
    override suspend fun invoke(businessId: Uuid): AppointmentSettings {
        return appointmentSettingsDataSource.getAppointmentSettingsFromDB(businessId)
            ?: appointmentSettingsDataSource.getAppointmentSettings(businessId)
                .also { appointmentSettingsDataSource.saveAppointmentSettingsInDB(it) }
    }
}

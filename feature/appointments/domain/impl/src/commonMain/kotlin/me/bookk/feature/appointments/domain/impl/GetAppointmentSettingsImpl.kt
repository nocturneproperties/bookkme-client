package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.datasource.AppointmentSettingsDataSource
import kotlin.uuid.Uuid

internal class GetAppointmentSettingsImpl(
    private val appointmentSettingsDataSource: AppointmentSettingsDataSource
) : GetAppointmentSettings {

    override fun flow(businessId: Uuid): Flow<AppointmentSettings?> =
        appointmentSettingsDataSource.observeAppointmentSettingsDBChanges(businessId)

    override suspend fun refresh(businessId: Uuid): AppointmentSettings {
        return appointmentSettingsDataSource.getAppointmentSettings(businessId)
            .also { appointmentSettingsDataSource.saveAppointmentSettingsInDB(it) }
    }
}

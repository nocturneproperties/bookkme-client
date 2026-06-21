package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.UpdateAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.datasource.AppointmentSettingsDataSource

internal class UpdateAppointmentSettingsImpl(
    private val appointmentSettingsDataSource: AppointmentSettingsDataSource
) : UpdateAppointmentSettings {

    override suspend fun invoke(settings: AppointmentSettings): AppointmentSettings {
        return runCatching {
            val result = appointmentSettingsDataSource.updateAppointmentSettings(settings)
            appointmentSettingsDataSource.saveAppointmentSettingsInDB(result)
            result
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.ACTIVE_DAY_WITHOUT_WORK_HOURS ->
                    throw UpdateAppointmentSettings.Error.ActiveDayWithoutWorkHours()
                AppointmentErrorCodes.INVALID_DAY_OFF_RANGE ->
                    throw UpdateAppointmentSettings.Error.InvalidDayOffRange()
            }
        }.getOrThrow()
    }
}

package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import kotlin.uuid.Uuid

internal class GetAppointmentSettingsImpl : GetAppointmentSettings {
    override suspend fun invoke(businessId: Uuid): AppointmentSettings {
        return AppointmentSettings.stub()
    }
}
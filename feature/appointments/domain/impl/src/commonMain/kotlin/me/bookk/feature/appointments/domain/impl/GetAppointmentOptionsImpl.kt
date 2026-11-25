package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import me.bookk.feature.services.domain.api.service.GetServices
import kotlin.uuid.Uuid

internal class GetAppointmentOptionsImpl(
    private val getServices: GetServices,
) : GetAppointmentOptions {
    override suspend fun invoke(businessId: Uuid): AppointmentOptions {
        return AppointmentOptions(services = getServices.refresh(businessId).snapshotServices())
    }
}

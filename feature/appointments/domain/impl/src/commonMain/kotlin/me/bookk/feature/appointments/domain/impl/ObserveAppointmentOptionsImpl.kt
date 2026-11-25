package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.ObserveAppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import me.bookk.feature.services.domain.api.service.GetServices

internal class ObserveAppointmentOptionsImpl(
    private val getServices: GetServices,
) : ObserveAppointmentOptions {
    override fun invoke(): Flow<AppointmentOptions> {
        return getServices.flow().map { AppointmentOptions(services = it.snapshotServices()) }
    }
}

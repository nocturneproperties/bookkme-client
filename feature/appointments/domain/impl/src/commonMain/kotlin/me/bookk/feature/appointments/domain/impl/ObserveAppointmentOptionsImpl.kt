package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.bookk.feature.appointments.domain.api.ObserveAppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.services.domain.api.service.GetServices

internal class ObserveAppointmentOptionsImpl(
    private val getClients: GetClientsList,
    private val getServices: GetServices
) : ObserveAppointmentOptions {
    override fun invoke(): Flow<AppointmentOptions> {
        return combine(getClients.flow(), getServices.flow()) { clients, services ->
            AppointmentOptions(
                clients = clients.snapshotClients(),
                services = services.snapshotServices()
            )
        }
    }
}

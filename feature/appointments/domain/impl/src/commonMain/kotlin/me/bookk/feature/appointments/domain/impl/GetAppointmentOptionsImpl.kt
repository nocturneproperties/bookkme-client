package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.services.domain.api.service.GetServices
import kotlin.uuid.Uuid

internal class GetAppointmentOptionsImpl(
    private val getClients: GetClientsList,
    private val getServices: GetServices
) : GetAppointmentOptions {
    override suspend fun invoke(businessId: Uuid): AppointmentOptions = coroutineScope {
        val clients = async { getClients.refresh(businessId).snapshotClients() }
        val services = async { getServices.refresh(businessId).snapshotServices() }
        AppointmentOptions(
            clients = clients.await(),
            services = services.await()
        )
    }
}

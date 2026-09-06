package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

internal class GetAppointmentOptionsImpl(
    private val getAppointmentSettings: GetAppointmentSettings,
    private val getClients: GetClientsList,
    private val getServices: GetServices
) : GetAppointmentOptions {
    override suspend fun invoke(businessId: Uuid): AppointmentOptions = coroutineScope {
        val clients = async { getClients.refresh(businessId).snapshotClients() }
        val services = async { getServices(businessId).snapshotServices() }
        val settings = async { getAppointmentSettings(businessId) }
        AppointmentOptions(
            settings = settings.await(),
            clients = clients.await(),
            services = services.await()
        )
    }

    private fun List<Service>.snapshotServices() = map {
        ServiceSnapshot(
            id = it.id,
            name = it.name,
            groupId = it.group.id,
            price = it.price,
            duration = it.duration
        )
    }

    private fun List<Client>.snapshotClients() = map {
        ClientSnapshot(
            id = it.id,
            fullName = it.fullName,
            phone = it.phone,
            email = it.email
        )
    }

}
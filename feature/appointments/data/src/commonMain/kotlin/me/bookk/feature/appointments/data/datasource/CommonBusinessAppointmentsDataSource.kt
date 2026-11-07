package me.bookk.feature.appointments.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import me.bookk.core.data.DataSource
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting
import me.bookk.feature.appointments.data.remote.model.BusinessAppointmentsEnabledRemote
import me.bookk.feature.appointments.domain.api.entity.BusinessAppointmentsEnabled
import me.bookk.feature.appointments.domain.datasource.BusinessAppointmentsDataSource

internal class CommonBusinessAppointmentsDataSource(
    private val httpClient: HttpClient,
) : DataSource(),
    BusinessAppointmentsDataSource {
    override suspend fun getEnabled(): List<BusinessAppointmentsEnabled> = mapExceptions {
        httpClient.get(AppointmentRouting.Api.Appointment.Enabled())
            .body<List<BusinessAppointmentsEnabledRemote>>()
            .map { it.toDomain() }
    }
}

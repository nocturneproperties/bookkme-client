package me.bookk.feature.appointments.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlinx.datetime.LocalDate
import me.bookk.core.data.DataSource
import me.bookk.database.dao.AppointmentDao
import me.bookk.feature.appointments.data.mapping.toEntity
import me.bookk.feature.appointments.data.mapping.toServiceEntities
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.appointments.data.remote.model.AppointmentRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid

internal class CommonAppointmentDataSource(
    private val httpClient: HttpClient,
    private val appointmentDao: AppointmentDao
) : DataSource(), AppointmentDataSource {

    override suspend fun getAppointmentsForDate(
        businessId: Uuid,
        forDate: LocalDate
    ): List<Appointment> =
        mapExceptions {
            httpClient.get(Api.Appointments(businessId = businessId, date = forDate))
                .body<List<AppointmentRemote>>()
                .map { it.toDomain() }
        }

    override suspend fun saveAppointmentsForDate(
        appointments: List<Appointment>,
        forDate: LocalDate
    ) = mapExceptions {
        appointmentDao.upsertWithServices(
            appointments = appointments.map { it.toEntity(forDate) },
            services = appointments.flatMap { it.toServiceEntities() }
        )
    }
}

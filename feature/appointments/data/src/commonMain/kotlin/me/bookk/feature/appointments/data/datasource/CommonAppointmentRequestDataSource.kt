package me.bookk.feature.appointments.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.feature.appointments.data.mapping.toRemote
import me.bookk.feature.appointments.data.mapping.toRequestEntity
import me.bookk.feature.appointments.data.mapping.toRequestServiceEntities
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.appointments.data.remote.model.AppointmentRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestIdRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.uuid.Uuid

internal class CommonAppointmentRequestDataSource(
    private val httpClient: HttpClient,
    private val appointmentRequestDao: AppointmentRequestDao
) : DataSource(), AppointmentRequestDataSource {

    override suspend fun createAppointmentRequest(request: AppointmentRequest): AppointmentRequest =
        mapExceptions {
            httpClient.post(Api.Appointment.Request()) {
                setBody(request.toRemote())
            }
                .body<AppointmentRequestRemote>()
                .toDomain()
        }

    override suspend fun createAppointmentFromRequest(requestId: Uuid): Appointment =
        mapExceptions {
            httpClient.post(Api.Appointment()) {
                setBody(AppointmentRequestIdRemote(requestId = requestId))
            }
                .body<AppointmentRemote>()
                .toDomain()
        }

    override suspend fun getAppointmentRequests(businessId: Uuid): List<AppointmentRequest> =
        mapExceptions {
            httpClient.get(Api.Appointment.Requests(businessId = businessId))
                .body<List<AppointmentRequestRemote>>()
                .map { it.toDomain() }
        }

    override suspend fun saveAppointmentRequestsInDB(requests: List<AppointmentRequest>) =
        mapExceptions {
            appointmentRequestDao.upsertWithServices(
                requests = requests.map { it.toRequestEntity() },
                services = requests.flatMap { it.toRequestServiceEntities() }
            )
        }
}

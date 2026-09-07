package me.bookk.feature.appointments.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.feature.appointments.data.mapping.toDomain
import me.bookk.feature.appointments.data.mapping.toRemote
import me.bookk.feature.appointments.data.mapping.toRequestEntity
import me.bookk.feature.appointments.data.mapping.toRequestServiceEntities
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.appointments.data.remote.model.AppointmentCancellationRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentOfferRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestIdRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal class CommonAppointmentRequestDataSource(
    private val httpClient: HttpClient,
    private val appointmentRequestDao: AppointmentRequestDao,
    preferenceProvider: PreferenceProvider
) : DataSource(), AppointmentRequestDataSource, LogOutAction {

    private val preferences = preferenceProvider.get("appointments_prefs")

    override suspend fun createAppointmentRequest(request: AppointmentRequest, offerToken: String) =
        mapExceptions {
            httpClient.post(Api.Appointment.Request()) {
                setBody(AppointmentOfferRemote(request = request.toRemote(), offerToken = offerToken))
            }
            Unit
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

    override fun observeAppointmentRequestsDBChanges(businessId: Uuid): Flow<List<AppointmentRequest>> {
        return appointmentRequestDao.observeForBusiness(businessId)
            .map { requests -> requests.map { it.toDomain() } }
            .mapErrors()
    }

    override suspend fun getAppointmentRequestIdsInDb(businessId: Uuid): List<Uuid> = mapExceptions {
        appointmentRequestDao.getIdsForBusiness(businessId)
    }

    override suspend fun deleteAppointmentRequestsInDb(ids: List<Uuid>) {
        mapExceptions {
            ids.chunked(DELETE_CHUNK_SIZE).forEach { chunk -> appointmentRequestDao.deleteByIds(chunk) }
        }
    }

    override suspend fun saveAppointmentRequestsInDB(requests: List<AppointmentRequest>) =
        mapExceptions {
            appointmentRequestDao.upsertWithServices(
                requests = requests.map { it.toRequestEntity() },
                services = requests.flatMap { it.toRequestServiceEntities() }
            )
        }

    override suspend fun declineAppointmentRequest(requestId: Uuid, businessId: Uuid, reason: String) =
        mapExceptions {
            httpClient.post(Api.Appointment.RequestDecline(id = requestId)) {
                setBody(AppointmentCancellationRemote(id = requestId, businessId = businessId, reason = reason))
            }
            Unit
        }

    override suspend fun getLastSyncedAt(businessId: Uuid): Instant? {
        return preferences.get(Key.lastSyncedAt(businessId))?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun saveLastSyncedAt(businessId: Uuid) {
        preferences.set(Key.lastSyncedAt(businessId), Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
        appointmentRequestDao.clear()
    }

    private object Key {
        fun lastSyncedAt(businessId: Uuid) = Preferences.Key<Long>("last_synced_at_$businessId")
    }
}

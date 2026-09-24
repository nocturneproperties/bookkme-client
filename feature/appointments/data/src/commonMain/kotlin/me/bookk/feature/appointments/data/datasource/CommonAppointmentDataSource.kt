package me.bookk.feature.appointments.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.AppointmentDao
import me.bookk.feature.appointments.data.mapping.toDomain
import me.bookk.feature.appointments.data.mapping.toEntity
import me.bookk.feature.appointments.data.mapping.toRemote
import me.bookk.feature.appointments.data.mapping.toServiceEntities
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.appointments.data.remote.model.AppointmentPaginationRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCancellation
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal class CommonAppointmentDataSource(
    private val httpClient: HttpClient,
    private val appointmentDao: AppointmentDao,
    preferenceProvider: PreferenceProvider
) : DataSource(), AppointmentDataSource, LogOutAction {

    private val preferences = preferenceProvider.get("appointments_prefs")

    override suspend fun getAppointment(id: Uuid): Appointment = mapExceptions {
        appointmentDao.getById(id).toDomain()
    }

    override suspend fun getAppointmentsForDate(
        businessId: Uuid,
        forDate: LocalDate
    ): List<Appointment> =
        mapExceptions {
            httpClient.get(Api.Appointment.List(businessId = businessId, date = forDate))
                .body<List<AppointmentRemote>>()
                .map { it.toDomain() }
        }

    override suspend fun getAppointmentHistory(
        businessId: Uuid,
        limit: Int,
        offset: Long,
        query: String?
    ): List<Appointment> =
        mapExceptions {
            httpClient.get(
                Api.Appointment.History(businessId = businessId, limit = limit, offset = offset, query = query)
            )
                .body<AppointmentPaginationRemote>()
                .data
                .map { it.toDomain() }
        }

    override suspend fun saveAppointmentsInDB(
        appointments: List<Appointment>
    ) = mapExceptions {
        appointmentDao.upsertWithServices(
            appointments = appointments.map { it.toEntity() },
            services = appointments.flatMap { it.toServiceEntities() }
        )
    }

    override suspend fun createAppointment(appointment: Appointment): Appointment =
        mapExceptions {
            httpClient.post(Api.Appointment.Instant()) {
                setBody(appointment.toRemote())
            }
                .body<AppointmentRemote>()
                .toDomain()
        }

    override suspend fun cancelAppointment(cancellation: AppointmentCancellation): Appointment =
        mapExceptions {
            httpClient.post(Api.Appointment.Cancel(id = cancellation.id)) {
                setBody(cancellation.toRemote())
            }
                .body<AppointmentRemote>()
                .toDomain()
                .also { appointmentDao.updateStatus(it.id, it.status.name, it.cancellationReason) }
        }

    override suspend fun updateAppointment(appointment: Appointment): Appointment =
        mapExceptions {
            httpClient.put(Api.Appointment.Id(id = appointment.id)) {
                setBody(appointment.toRemote())
            }
                .body<AppointmentRemote>()
                .toDomain()
        }

    override suspend fun saveAppointmentInDB(appointment: Appointment) {
        mapExceptions {
            appointmentDao.upsertWithServices(
                appointments = listOf(appointment.toEntity()),
                services = appointment.toServiceEntities()
            )
        }
    }

    override fun observeAppointmentsForDateDBChanges(
        businessId: Uuid,
        forDate: LocalDate
    ): Flow<List<Appointment>> {
        val (startOfDay, endOfDay) = forDate.dayBounds()
        return appointmentDao.observeForDate(businessId, startOfDay, endOfDay)
            .map { appointments -> appointments.map { it.toDomain() } }
            .mapErrors()
    }

    override suspend fun getAppointmentIdsForDateInDb(
        businessId: Uuid,
        forDate: LocalDate
    ): List<Uuid> = mapExceptions {
        val (startOfDay, endOfDay) = forDate.dayBounds()
        appointmentDao.getIdsForDate(businessId, startOfDay, endOfDay)
    }

    override suspend fun deleteAppointmentsInDb(ids: List<Uuid>) {
        mapExceptions {
            ids.chunked(DELETE_CHUNK_SIZE).forEach { chunk -> appointmentDao.deleteByIds(chunk) }
        }
    }

    private fun LocalDate.dayBounds(): Pair<Instant, Instant> {
        val timeZone = TimeZone.currentSystemDefault()
        val startOfDay = atStartOfDayIn(timeZone)
        val endOfDay = plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
        return startOfDay to endOfDay
    }

    override suspend fun getLastSyncedAt(businessId: Uuid, forDate: LocalDate): Instant? {
        return preferences.get(Key.lastSyncedAt(businessId, forDate))?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun saveLastSyncedAt(businessId: Uuid, forDate: LocalDate) {
        preferences.set(Key.lastSyncedAt(businessId, forDate), Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
        appointmentDao.clear()
    }

    private object Key {
        fun lastSyncedAt(businessId: Uuid, forDate: LocalDate) =
            Preferences.Key<Long>("last_synced_at_${businessId}_$forDate")
    }
}

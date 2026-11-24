package me.bookk.feature.appointments.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.SavedBusinessDao
import me.bookk.feature.appointments.data.mapping.toAdjustmentServiceEntities
import me.bookk.feature.appointments.data.mapping.toDomain
import me.bookk.feature.appointments.data.mapping.toEntity
import me.bookk.feature.appointments.data.mapping.toRequestEntity
import me.bookk.feature.appointments.data.mapping.toRequestServiceEntities
import me.bookk.feature.appointments.data.mapping.toServiceEntities
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.appointments.data.remote.model.FreeSlotsDayRemote
import me.bookk.feature.appointments.data.remote.model.FreeSlotsRequestRemote
import me.bookk.feature.appointments.data.remote.model.SavedBusinessRemote
import me.bookk.feature.appointments.data.remote.model.UpcomingBookingsRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import kotlin.uuid.Uuid

internal class CommonClientBookingDataSource(
    private val httpClient: HttpClient,
    private val appointmentDao: AppointmentDao,
    private val appointmentRequestDao: AppointmentRequestDao,
    private val savedBusinessDao: SavedBusinessDao,
) : DataSource(),
    ClientBookingDataSource,
    LogOutAction {

    override suspend fun getUpcomingBookings() = mapExceptions {
        httpClient.get(Api.Appointment.Upcoming())
            .body<UpcomingBookingsRemote>()
            .toDomain()
    }

    override suspend fun saveUpcomingBookingsInDb(bookings: UpcomingBookings) {
        mapExceptions {
            replaceScheduledAppointments(bookings.appointments)
            replacePendingRequests(bookings.requests)
        }
    }

    private suspend fun replaceScheduledAppointments(appointments: List<Appointment>) {
        val freshIds = appointments.map { it.id }.toSet()
        val staleIds = appointmentDao.getScheduledIds().filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) appointmentDao.deleteByIds(staleIds)
        appointmentDao.upsertWithServices(
            appointments = appointments.map { it.toEntity() },
            services = appointments.flatMap { it.toServiceEntities() },
            adjustmentServices = appointments.flatMap { it.toAdjustmentServiceEntities() },
        )
    }

    private suspend fun replacePendingRequests(requests: List<AppointmentRequest>) {
        val freshIds = requests.map { it.id }.toSet()
        val staleIds = appointmentRequestDao.getPendingIds().filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) appointmentRequestDao.deleteByIds(staleIds)
        appointmentRequestDao.upsertWithServices(
            requests = requests.map { it.toRequestEntity() },
            services = requests.flatMap { it.toRequestServiceEntities() },
        )
    }

    override fun observeUpcomingAppointments(): Flow<List<Appointment>> {
        return appointmentDao.observeScheduled()
            .map { appointments -> appointments.map { it.toDomain() } }
            .mapErrors()
    }

    override fun observeUpcomingRequests(): Flow<List<AppointmentRequest>> {
        return appointmentRequestDao.observePending()
            .map { requests -> requests.map { it.toDomain() } }
            .mapErrors()
    }

    override suspend fun getSavedBusinesses() = mapExceptions {
        httpClient.get(Api.Appointment.SavedBusinesses())
            .body<List<SavedBusinessRemote>>()
            .map { it.toDomain() }
    }

    override suspend fun saveSavedBusinessesInDb(businesses: List<SavedBusinessEntry>) {
        mapExceptions {
            savedBusinessDao.replaceAll(businesses.map { it.toEntity() })
        }
    }

    override fun observeSavedBusinesses(): Flow<List<SavedBusinessEntry>> {
        return savedBusinessDao.observeAll()
            .map { businesses -> businesses.map { it.toDomain() } }
            .mapErrors()
    }

    override suspend fun saveBusiness(businessId: Uuid) {
        mapExceptions {
            httpClient.post(Api.Appointment.SavedBusinesses.Id(businessId = businessId))
        }
    }

    override suspend fun getFreeSlots(query: FreeSlotsQuery) = mapExceptions {
        httpClient.post(Api.Appointment.Slots()) {
            setBody(FreeSlotsRequestRemote.fromDomain(query))
        }
            .body<List<FreeSlotsDayRemote>>()
            .map { it.toDomain() }
    }

    override suspend fun doOnLogOut() {
        appointmentDao.clear()
        appointmentRequestDao.clear()
        savedBusinessDao.clear()
    }
}

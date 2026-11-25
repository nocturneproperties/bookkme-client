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
import me.bookk.database.dao.BookingInfoDao
import me.bookk.database.dao.SavedBusinessDao
import me.bookk.feature.appointments.data.mapping.appointmentStatusNames
import me.bookk.feature.appointments.data.mapping.appointmentStatuses
import me.bookk.feature.appointments.data.mapping.requestStatusNames
import me.bookk.feature.appointments.data.mapping.requestStatuses
import me.bookk.feature.appointments.data.mapping.toAdjustmentServiceEntities
import me.bookk.feature.appointments.data.mapping.toDomain
import me.bookk.feature.appointments.data.mapping.toEntity
import me.bookk.feature.appointments.data.mapping.toRemote
import me.bookk.feature.appointments.data.mapping.toRequestEntity
import me.bookk.feature.appointments.data.mapping.toRequestServiceEntities
import me.bookk.feature.appointments.data.mapping.toServiceEntities
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.appointments.data.remote.model.AppointmentPageRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestPageRemote
import me.bookk.feature.appointments.data.remote.model.BookingInfoRemote
import me.bookk.feature.appointments.data.remote.model.FreeSlotsDayRemote
import me.bookk.feature.appointments.data.remote.model.FreeSlotsRequestRemote
import me.bookk.feature.appointments.data.remote.model.SavedBusinessRemote
import me.bookk.feature.appointments.data.remote.model.UpcomingBookingsRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.BookingInfo
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.appointments.domain.datasource.DateBounds
import kotlin.uuid.Uuid

internal class CommonClientBookingDataSource(
    private val httpClient: HttpClient,
    private val appointmentDao: AppointmentDao,
    private val appointmentRequestDao: AppointmentRequestDao,
    private val savedBusinessDao: SavedBusinessDao,
    private val bookingInfoDao: BookingInfoDao,
) : DataSource(),
    ClientBookingDataSource,
    LogOutAction {

    override suspend fun getUpcomingBookings() = mapExceptions {
        httpClient.get(Api.Appointment.Upcoming())
            .body<UpcomingBookingsRemote>()
            .toDomain()
    }

    override suspend fun getOwnAppointments(period: ClientBookingPeriod, cursor: String?) =
        mapExceptions {
            val resource = Api.Appointment.Mine(
                cursor = cursor,
                period = period.toRemote(),
                status = period.appointmentStatuses(),
            )
            httpClient.get(resource)
                .body<AppointmentPageRemote>()
                .toDomain()
        }

    override suspend fun getOwnRequests(period: ClientBookingPeriod, cursor: String?) =
        mapExceptions {
            val resource = Api.Appointment.RequestMine(
                cursor = cursor,
                period = period.toRemote(),
                status = period.requestStatuses(),
            )
            httpClient.get(resource)
                .body<AppointmentRequestPageRemote>()
                .toDomain()
        }

    override suspend fun saveAppointmentsInDb(appointments: List<Appointment>) {
        mapExceptions {
            appointmentDao.upsertWithServices(
                appointments = appointments.map { it.toEntity() },
                services = appointments.flatMap { it.toServiceEntities() },
                adjustmentServices = appointments.flatMap { it.toAdjustmentServiceEntities() },
            )
        }
    }

    override suspend fun saveRequestsInDb(requests: List<AppointmentRequest>) {
        mapExceptions {
            appointmentRequestDao.upsertWithServices(
                requests = requests.map { it.toRequestEntity() },
                services = requests.flatMap { it.toRequestServiceEntities() },
            )
        }
    }

    override suspend fun getAppointmentIdsInDb(period: ClientBookingPeriod, dates: DateBounds) =
        mapExceptions {
            appointmentDao.getIdsByStatusesBetween(
                period.appointmentStatusNames(),
                dates.from,
                dates.to,
            )
        }

    override suspend fun getRequestIdsInDb(period: ClientBookingPeriod, dates: DateBounds) =
        mapExceptions {
            appointmentRequestDao.getIdsByStatusesBetween(
                period.requestStatusNames(),
                dates.from,
                dates.to,
            )
        }

    override suspend fun deleteAppointmentsInDb(ids: List<Uuid>) {
        mapExceptions { appointmentDao.deleteByIds(ids) }
    }

    override suspend fun deleteRequestsInDb(ids: List<Uuid>) {
        mapExceptions { appointmentRequestDao.deleteByIds(ids) }
    }

    override fun observeAppointments(period: ClientBookingPeriod): Flow<List<Appointment>> {
        return appointmentDao.observeByStatuses(period.appointmentStatusNames())
            .map { appointments -> appointments.map { it.toDomain() } }
            .mapErrors()
    }

    override fun observeRequests(period: ClientBookingPeriod): Flow<List<AppointmentRequest>> {
        return appointmentRequestDao.observeByStatuses(period.requestStatusNames())
            .map { requests -> requests.map { it.toDomain() } }
            .mapErrors()
    }

    override fun observeAppointment(appointmentId: Uuid): Flow<Appointment?> {
        return appointmentDao.observeById(appointmentId)
            .map { it?.toDomain() }
            .mapErrors()
    }

    override fun observeRequest(requestId: Uuid): Flow<AppointmentRequest?> {
        return appointmentRequestDao.observeById(requestId)
            .map { it?.toDomain() }
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

    override suspend fun getBookingInfo(businessId: Uuid) = mapExceptions {
        httpClient.get(Api.Appointment.BookingInfo(businessId = businessId))
            .body<BookingInfoRemote>()
            .toDomain()
    }

    override suspend fun saveBookingInfoInDb(info: BookingInfo) {
        mapExceptions { bookingInfoDao.upsert(info.toEntity()) }
    }

    override suspend fun deleteBookingInfoInDb(businessId: Uuid) {
        mapExceptions { bookingInfoDao.delete(businessId) }
    }

    override fun observeBookingInfo(businessId: Uuid): Flow<BookingInfo?> {
        return bookingInfoDao.observe(businessId)
            .map { it?.toDomain() }
            .mapErrors()
    }

    override suspend fun doOnLogOut() {
        appointmentDao.clear()
        appointmentRequestDao.clear()
        savedBusinessDao.clear()
        bookingInfoDao.clear()
    }
}

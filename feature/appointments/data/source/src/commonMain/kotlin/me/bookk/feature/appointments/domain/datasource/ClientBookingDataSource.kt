package me.bookk.feature.appointments.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.pagination.Page
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.BookingInfo
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsDay
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry
import me.bookk.feature.appointments.domain.api.entity.UpcomingBookings
import kotlin.uuid.Uuid

interface ClientBookingDataSource {
    suspend fun getUpcomingBookings(): UpcomingBookings

    suspend fun getOwnAppointments(period: ClientBookingPeriod, cursor: String?): Page<Appointment>

    suspend fun getOwnRequests(
        period: ClientBookingPeriod,
        cursor: String?,
    ): Page<AppointmentRequest>

    suspend fun saveAppointmentsInDb(appointments: List<Appointment>)

    suspend fun saveRequestsInDb(requests: List<AppointmentRequest>)

    suspend fun getAppointmentIdsInDb(period: ClientBookingPeriod, dates: DateBounds): List<Uuid>

    suspend fun getRequestIdsInDb(period: ClientBookingPeriod, dates: DateBounds): List<Uuid>

    suspend fun deleteAppointmentsInDb(ids: List<Uuid>)

    suspend fun deleteRequestsInDb(ids: List<Uuid>)

    fun observeAppointments(period: ClientBookingPeriod): Flow<List<Appointment>>

    fun observeRequests(period: ClientBookingPeriod): Flow<List<AppointmentRequest>>

    fun observeAppointment(appointmentId: Uuid): Flow<Appointment?>

    fun observeRequest(requestId: Uuid): Flow<AppointmentRequest?>

    suspend fun getSavedBusinesses(): List<SavedBusinessEntry>

    suspend fun saveSavedBusinessesInDb(businesses: List<SavedBusinessEntry>)

    fun observeSavedBusinesses(): Flow<List<SavedBusinessEntry>>

    suspend fun saveBusiness(businessId: Uuid)

    suspend fun getFreeSlots(query: FreeSlotsQuery): List<FreeSlotsDay>

    suspend fun getBookingInfo(businessId: Uuid): BookingInfo

    suspend fun saveBookingInfoInDb(info: BookingInfo)

    suspend fun deleteBookingInfoInDb(businessId: Uuid)

    fun observeBookingInfo(businessId: Uuid): Flow<BookingInfo?>
}

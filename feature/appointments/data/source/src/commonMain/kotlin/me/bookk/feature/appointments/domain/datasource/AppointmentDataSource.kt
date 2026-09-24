package me.bookk.feature.appointments.domain.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCancellation
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface AppointmentDataSource {
    suspend fun getAppointment(id: Uuid): Appointment

    suspend fun getAppointmentsForDate(
        businessId: Uuid,
        forDate: LocalDate
    ): List<Appointment>

    fun observeAppointmentsForDateDBChanges(
        businessId: Uuid,
        forDate: LocalDate
    ): Flow<List<Appointment>>

    suspend fun getAppointmentIdsForDateInDb(
        businessId: Uuid,
        forDate: LocalDate
    ): List<Uuid>

    suspend fun deleteAppointmentsInDb(ids: List<Uuid>)

    suspend fun getAppointmentHistory(
        businessId: Uuid,
        limit: Int,
        offset: Long,
        query: String? = null
    ): List<Appointment>

    suspend fun createAppointment(appointment: Appointment): Appointment

    suspend fun cancelAppointment(cancellation: AppointmentCancellation): Appointment

    suspend fun updateAppointment(appointment: Appointment): Appointment

    suspend fun saveAppointmentsInDB(appointments: List<Appointment>)

    suspend fun saveAppointmentInDB(appointment: Appointment)

    suspend fun getLastSyncedAt(businessId: Uuid, forDate: LocalDate): Instant?

    suspend fun saveLastSyncedAt(businessId: Uuid, forDate: LocalDate)
}

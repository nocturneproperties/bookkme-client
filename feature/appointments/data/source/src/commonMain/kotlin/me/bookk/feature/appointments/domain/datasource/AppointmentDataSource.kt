package me.bookk.feature.appointments.domain.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import me.bookk.core.domain.pagination.Page
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCancellation
import me.bookk.feature.appointments.domain.api.entity.InstantAppointmentDraft
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustmentDraft
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface AppointmentDataSource {
    suspend fun getAppointment(id: Uuid): Appointment

    suspend fun getAppointmentFromRemote(id: Uuid): Appointment

    suspend fun getAppointmentsForDate(
        businessId: Uuid,
        forDate: LocalDate,
        employeeId: Uuid?,
    ): List<Appointment>

    fun observeAppointmentsForDateDBChanges(
        businessId: Uuid,
        forDate: LocalDate,
        employeeId: Uuid?,
    ): Flow<List<Appointment>>

    suspend fun getAppointmentIdsForDateInDb(
        businessId: Uuid,
        forDate: LocalDate,
        employeeId: Uuid?,
    ): List<Uuid>

    suspend fun deleteAppointmentsInDb(ids: List<Uuid>)

    suspend fun getAppointmentHistory(
        businessId: Uuid,
        search: String?,
        cursor: String?,
    ): Page<Appointment>

    fun observeAppointmentsDBChanges(businessId: Uuid): Flow<List<Appointment>>

    suspend fun getAppointmentIdsInDb(
        businessId: Uuid,
        from: LocalDateTime?,
        to: LocalDateTime?,
    ): List<Uuid>

    suspend fun createAppointment(draft: InstantAppointmentDraft): Appointment

    suspend fun cancelAppointment(cancellation: AppointmentCancellation): Appointment

    suspend fun updateAppointment(appointment: Appointment): Appointment

    suspend fun completeAppointment(id: Uuid, priceAdjustment: PriceAdjustmentDraft?): Appointment

    suspend fun markAppointmentNoShow(id: Uuid): Appointment

    suspend fun saveAppointmentsInDB(appointments: List<Appointment>)

    suspend fun saveAppointmentInDB(appointment: Appointment)

    suspend fun getLastSyncedAt(businessId: Uuid, forDate: LocalDate): Instant?

    suspend fun saveLastSyncedAt(businessId: Uuid, forDate: LocalDate)
}

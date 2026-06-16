package me.bookk.feature.appointments.domain.datasource

import kotlinx.datetime.LocalDate
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCancellation
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.uuid.Uuid

interface AppointmentDataSource {
    suspend fun getAppointment(id: Uuid): Appointment

    suspend fun getAppointmentsForDate(
        businessId: Uuid,
        forDate: LocalDate
    ): List<Appointment>

    suspend fun saveAppointmentsForDate(
        appointments: List<Appointment>,
        forDate: LocalDate
    )

    suspend fun createAppointmentRequest(request: AppointmentRequest): AppointmentRequest

    suspend fun createAppointmentFromRequest(requestId: Uuid): Appointment

    suspend fun createAppointment(appointment: Appointment): Appointment

    suspend fun cancelAppointment(cancellation: AppointmentCancellation): Appointment
}

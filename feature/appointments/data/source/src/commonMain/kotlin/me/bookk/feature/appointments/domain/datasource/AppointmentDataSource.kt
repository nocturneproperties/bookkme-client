package me.bookk.feature.appointments.domain.datasource

import kotlinx.datetime.LocalDate
import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface AppointmentDataSource {
    suspend fun getAppointmentsForDate(
        businessId: Uuid,
        forDate: LocalDate
    ): List<Appointment>

    suspend fun saveAppointmentsForDate(
        appointments: List<Appointment>,
        forDate: LocalDate
    )
}

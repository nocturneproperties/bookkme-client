package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface GetAppointmentsForBusiness {
    fun flow(date: LocalDate, employeeId: Uuid? = null): Flow<List<Appointment>>
    suspend fun refresh(businessId: Uuid, date: LocalDate, employeeId: Uuid? = null): List<Appointment>
}

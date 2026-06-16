package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface GetAppointmentsForDashboardBusiness {
    suspend operator fun invoke(date: LocalDate): List<Appointment>
    suspend fun businessId(): Uuid?
    fun flow(date: LocalDate): Flow<Result<List<Appointment>>>
}

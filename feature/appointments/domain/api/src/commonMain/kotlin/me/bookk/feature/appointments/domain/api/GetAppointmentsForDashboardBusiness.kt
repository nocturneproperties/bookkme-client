package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import me.bookk.feature.appointments.domain.api.entity.Appointment

interface GetAppointmentsForDashboardBusiness {
    suspend operator fun invoke(date: LocalDate): List<Appointment>
    fun flow(date: LocalDate): Flow<List<Appointment>>
}

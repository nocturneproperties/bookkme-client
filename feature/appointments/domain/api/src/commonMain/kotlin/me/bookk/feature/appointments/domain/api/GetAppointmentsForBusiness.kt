package me.bookk.feature.appointments.domain.api

import kotlinx.datetime.LocalDate
import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface GetAppointmentsForBusiness {
    suspend operator fun invoke(businessId: Uuid, date: LocalDate): List<Appointment>
}

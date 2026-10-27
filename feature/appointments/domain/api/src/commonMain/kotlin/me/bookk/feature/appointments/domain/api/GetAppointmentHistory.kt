package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.uuid.Uuid

interface GetAppointmentHistory {
    suspend fun reload(businessId: Uuid, query: String? = null): List<Appointment>
    suspend fun loadMore(): List<Appointment>
}

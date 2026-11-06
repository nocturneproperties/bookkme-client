package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.uuid.Uuid

interface GetBusinessAppointmentRequests {
    suspend operator fun invoke(businessId: Uuid): List<AppointmentRequest>
}

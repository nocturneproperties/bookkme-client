package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.uuid.Uuid

interface GetAppointmentRequests {
    suspend operator fun invoke(businessId: Uuid): List<AppointmentRequest>
    suspend fun cached(businessId: Uuid, onResultAvailable: suspend (List<AppointmentRequest>) -> Unit)
}

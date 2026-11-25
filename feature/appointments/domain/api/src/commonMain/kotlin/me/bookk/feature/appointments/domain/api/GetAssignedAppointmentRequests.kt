package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.pagination.PagedSource
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.uuid.Uuid

interface GetAssignedAppointmentRequests : PagedSource<Uuid, AppointmentRequest> {
    fun flow(businessId: Uuid): Flow<List<AppointmentRequest>>
}

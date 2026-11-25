package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.pagination.PagedSource
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentHistoryQuery
import kotlin.uuid.Uuid

interface GetAppointmentHistory : PagedSource<AppointmentHistoryQuery, Appointment> {
    fun flow(businessId: Uuid, search: String? = null): Flow<List<Appointment>>
}

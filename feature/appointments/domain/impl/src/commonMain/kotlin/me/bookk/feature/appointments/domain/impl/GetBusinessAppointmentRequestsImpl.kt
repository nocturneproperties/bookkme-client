package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.GetBusinessAppointmentRequests
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.uuid.Uuid

internal class GetBusinessAppointmentRequestsImpl(
    private val dataSource: AppointmentRequestDataSource
) : GetBusinessAppointmentRequests {
    override suspend fun invoke(businessId: Uuid): List<AppointmentRequest> {
        return dataSource.getBusinessAppointmentRequests(businessId)
    }
}

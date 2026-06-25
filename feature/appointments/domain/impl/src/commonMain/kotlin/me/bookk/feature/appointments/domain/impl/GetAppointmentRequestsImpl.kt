package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.GetAppointmentRequests
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.uuid.Uuid

internal class GetAppointmentRequestsImpl(
    private val dataSource: AppointmentRequestDataSource
) : GetAppointmentRequests {

    override suspend fun invoke(businessId: Uuid): List<AppointmentRequest> =
        dataSource.getAppointmentRequests(businessId)
            .also { dataSource.saveAppointmentRequestsInDB(it) }
}

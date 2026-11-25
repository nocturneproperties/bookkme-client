package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.GetClientAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import kotlin.uuid.Uuid

internal class GetClientAppointmentRequestImpl(
    private val dataSource: ClientBookingDataSource,
) : GetClientAppointmentRequest {

    override fun flow(requestId: Uuid): Flow<AppointmentRequest?> {
        return dataSource.observeRequest(requestId)
    }
}

package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.GetClientAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ClientAppointmentRequest
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.business.domain.api.business.GetBusinesses
import kotlin.uuid.Uuid

internal class GetClientAppointmentRequestImpl(
    private val dataSource: ClientBookingDataSource,
    private val getBusinesses: GetBusinesses,
) : GetClientAppointmentRequest {

    override fun flow(requestId: Uuid): Flow<ClientAppointmentRequest?> {
        return dataSource.observeRequest(requestId).flatMapLatest { request ->
            when (request) {
                null -> flowOf(null)

                else -> getBusinesses.nameOf(request.businessId)
                    .map { name -> ClientAppointmentRequest(request, name) }
            }
        }
    }
}

package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.GetClientAppointment
import me.bookk.feature.appointments.domain.api.entity.ClientAppointment
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.business.domain.api.business.GetBusinesses
import kotlin.uuid.Uuid

internal class GetClientAppointmentImpl(
    private val dataSource: ClientBookingDataSource,
    private val getBusinesses: GetBusinesses,
) : GetClientAppointment {

    override fun flow(appointmentId: Uuid): Flow<ClientAppointment?> {
        return dataSource.observeAppointment(appointmentId).flatMapLatest { appointment ->
            when (appointment) {
                null -> flowOf(null)

                else -> getBusinesses.nameOf(appointment.businessId)
                    .map { name -> ClientAppointment(appointment, name) }
            }
        }
    }
}

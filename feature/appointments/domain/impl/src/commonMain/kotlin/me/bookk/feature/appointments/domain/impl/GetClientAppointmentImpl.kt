package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.GetClientAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import kotlin.uuid.Uuid

internal class GetClientAppointmentImpl(
    private val dataSource: ClientBookingDataSource,
) : GetClientAppointment {

    override fun flow(appointmentId: Uuid): Flow<Appointment?> {
        return dataSource.observeAppointment(appointmentId)
    }
}

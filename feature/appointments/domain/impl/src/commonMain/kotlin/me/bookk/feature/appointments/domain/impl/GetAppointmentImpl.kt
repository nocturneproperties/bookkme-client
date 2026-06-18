package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid

internal class GetAppointmentImpl(
    private val appointmentDataSource: AppointmentDataSource
) : GetAppointment {
    override suspend fun invoke(id: Uuid): Appointment {
        return appointmentDataSource.getAppointment(id)
    }
}

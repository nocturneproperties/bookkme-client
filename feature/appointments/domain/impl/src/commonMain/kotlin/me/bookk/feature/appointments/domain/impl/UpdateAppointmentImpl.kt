package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.appointmentEvents
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource

internal class UpdateAppointmentImpl(
    private val appointmentDataSource: AppointmentDataSource
) : UpdateAppointment {

    override suspend fun invoke(appointment: Appointment): Appointment {
        appointmentDataSource.updateAppointment(appointment)
        appointmentDataSource.saveAppointmentInDB(appointment)
        appointmentEvents.emit(AppointmentEvent.Updated(appointment))
        return appointment
    }
}

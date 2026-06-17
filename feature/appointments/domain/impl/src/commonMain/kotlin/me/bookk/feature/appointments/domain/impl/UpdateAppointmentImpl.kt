package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.appointmentEvents
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource

internal class UpdateAppointmentImpl(
    private val appointmentDataSource: AppointmentDataSource
) : UpdateAppointment {

    override suspend fun invoke(appointment: Appointment): Appointment {
        return runCatching {
            val result = appointmentDataSource.updateAppointment(appointment)
            appointmentDataSource.saveAppointmentInDB(result)
            appointmentEvents.emit(AppointmentEvent.Updated(result))
            appointment
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.DATE_NOT_ALLOWED -> throw UpdateAppointment.Error.DateIsNotAllowed()
                AppointmentErrorCodes.TIME_NOT_ALLOWED -> throw UpdateAppointment.Error.TimeIsNotAllowed()
                AppointmentErrorCodes.APPOINTMENT_EXISTS -> throw UpdateAppointment.Error.AppointmentOverlap()
            }
        }.getOrThrow()
    }
}

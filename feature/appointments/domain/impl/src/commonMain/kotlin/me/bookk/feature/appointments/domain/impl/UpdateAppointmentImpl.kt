package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource

internal class UpdateAppointmentImpl(
    private val appointmentDataSource: AppointmentDataSource
) : UpdateAppointment {

    override suspend fun invoke(appointment: Appointment): Appointment {
        return runCatching {
            val result = appointmentDataSource.updateAppointment(appointment)
            appointmentDataSource.saveAppointmentInDB(result)
            result
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.DATE_NOT_ALLOWED -> throw UpdateAppointment.Error.DateIsNotAllowed()
                AppointmentErrorCodes.TIME_NOT_ALLOWED -> throw UpdateAppointment.Error.TimeIsNotAllowed()
                AppointmentErrorCodes.APPOINTMENT_EXISTS -> throw UpdateAppointment.Error.AppointmentOverlap()
                AppointmentErrorCodes.APPOINTMENT_ALREADY_CANCELED,
                AppointmentErrorCodes.APPOINTMENT_ALREADY_COMPLETED,
                AppointmentErrorCodes.APPOINTMENT_MARKED_NO_SHOW -> throw UpdateAppointment.Error.AppointmentNotScheduled()
                AppointmentErrorCodes.BUSINESS_EMPLOYEE_SUSPENDED -> throw UpdateAppointment.Error.EmployeeSuspended()
            }
        }.getOrThrow()
    }
}

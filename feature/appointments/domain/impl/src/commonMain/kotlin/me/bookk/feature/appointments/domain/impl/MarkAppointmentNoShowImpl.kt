package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.MarkAppointmentNoShow
import me.bookk.feature.appointments.domain.api.MarkAppointmentNoShow.Error
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid

internal class MarkAppointmentNoShowImpl(
    private val appointmentDataSource: AppointmentDataSource
) : MarkAppointmentNoShow {

    override suspend fun invoke(appointmentId: Uuid): Appointment {
        return runCatching {
            appointmentDataSource.markAppointmentNoShow(appointmentId).also {
                appointmentDataSource.saveAppointmentInDB(it)
            }
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.APPOINTMENT_ALREADY_CANCELED -> throw Error.AppointmentAlreadyCancelled()
                AppointmentErrorCodes.APPOINTMENT_NOT_STARTED -> throw Error.AppointmentNotStarted()
            }
        }.getOrThrow()
    }
}

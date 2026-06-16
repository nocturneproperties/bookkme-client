package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.CancelAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCancellation
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.appointmentEvents
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid

internal class CancelAppointmentImpl(
    private val appointmentDataSource: AppointmentDataSource
) : CancelAppointment {

    override suspend fun invoke(appointmentId: Uuid, reason: String): Appointment {
        val businessId = appointmentDataSource.getAppointment(appointmentId).businessId
        val cancellation = AppointmentCancellation(
            id = appointmentId,
            businessId = businessId,
            reason = reason
        )
        return runCatching {
            appointmentDataSource.cancelAppointment(cancellation).also {
                appointmentEvents.emit(AppointmentEvent.Cancelled(it))
            }
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.APPOINTMENT_ALREADY_CANCELED -> throw CancelAppointment.Error.AppointmentAlreadyCancelled()
                AppointmentErrorCodes.APPOINTMENT_ALREADY_COMPLETED -> throw CancelAppointment.Error.AppointmentAlreadyCompleted()
            }
        }.getOrThrow()
    }
}

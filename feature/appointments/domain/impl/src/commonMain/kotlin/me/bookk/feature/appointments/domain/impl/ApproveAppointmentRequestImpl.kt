package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.ApproveAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.appointmentEvents
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.uuid.Uuid

internal class ApproveAppointmentRequestImpl(
    private val dataSource: AppointmentRequestDataSource
) : ApproveAppointmentRequest {

    override suspend fun invoke(requestId: Uuid): Appointment {
        return runCatching {
            dataSource.createAppointmentFromRequest(requestId).also {
                appointmentEvents.emit(AppointmentEvent.Created(it))
            }
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.APPOINTMENT_EXISTS -> throw ApproveAppointmentRequest.Error.AppointmentExists()
                AppointmentErrorCodes.DATE_NOT_ALLOWED -> throw ApproveAppointmentRequest.Error.DateNotAllowed()
                AppointmentErrorCodes.TIME_NOT_ALLOWED -> throw ApproveAppointmentRequest.Error.TimeNotAllowed()
                AppointmentErrorCodes.DATE_IN_PAST -> throw ApproveAppointmentRequest.Error.DateInPast()
            }
        }.getOrThrow()
    }
}

package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.GetClientAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.api.entity.ClientAppointmentRequest
import me.bookk.feature.booking.domain.api.GetBookingRequestDetails
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails.Status
import kotlin.uuid.Uuid

internal class GetBookingRequestDetailsImpl(
    private val getClientAppointmentRequest: GetClientAppointmentRequest,
) : GetBookingRequestDetails {

    override fun flow(requestId: Uuid): Flow<BookingRequestDetails?> {
        return getClientAppointmentRequest.flow(requestId)
            .map { cached -> cached?.takeIf { it.request.services.isNotEmpty() }?.toDetails() }
            .distinctUntilChanged()
    }

    private fun ClientAppointmentRequest.toDetails(): BookingRequestDetails {
        val visit = BookedVisit(
            businessName = businessName,
            employee = request.employee,
            startsAt = request.date,
            duration = request.services.totalDuration(),
            services = request.services,
            total = request.services.totalPrice(),
            note = request.note,
        )
        return BookingRequestDetails(request.id, visit.toSummary(), request.status.toDetails())
    }

    private fun AppointmentRequestStatus.toDetails(): Status {
        return when (this) {
            AppointmentRequestStatus.PENDING -> Status.WAITING_FOR_CONFIRMATION
            AppointmentRequestStatus.APPROVED -> Status.CONFIRMED
            AppointmentRequestStatus.DECLINED -> Status.DECLINED
            AppointmentRequestStatus.CANCELLED -> Status.CLOSED
        }
    }
}

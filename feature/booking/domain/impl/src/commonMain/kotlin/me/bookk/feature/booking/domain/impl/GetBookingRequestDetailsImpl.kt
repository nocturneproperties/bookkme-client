package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.GetClientAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.booking.domain.api.GetBookingRequestDetails
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails
import kotlin.uuid.Uuid

internal class GetBookingRequestDetailsImpl(
    private val getClientAppointmentRequest: GetClientAppointmentRequest,
) : GetBookingRequestDetails {

    override fun flow(requestId: Uuid): Flow<BookingRequestDetails?> {
        return getClientAppointmentRequest.flow(requestId)
            .map { cached -> cached?.takeIf { it.services.isNotEmpty() }?.toDetails() }
            .distinctUntilChanged()
    }

    private fun AppointmentRequest.toDetails(): BookingRequestDetails {
        return BookingRequestDetails(id, toVisit().toSummary(), status.toBookingStatus())
    }
}

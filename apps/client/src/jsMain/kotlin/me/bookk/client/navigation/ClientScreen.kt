package me.bookk.client.navigation

import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import kotlin.uuid.Uuid

sealed interface ClientScreen {
    data object Home : ClientScreen
    data class Services(
        val businessId: Uuid,
    ) : ClientScreen

    data class Specialist(
        val businessId: Uuid,
    ) : ClientScreen

    data class DateTime(
        val businessId: Uuid,
    ) : ClientScreen

    data class Review(
        val businessId: Uuid,
    ) : ClientScreen

    data class Sent(
        val request: SentBookingRequest,
    ) : ClientScreen

    data class BookingRequestDetails(
        val requestId: Uuid,
    ) : ClientScreen

    data class BookingAppointmentDetails(
        val appointmentId: Uuid,
    ) : ClientScreen
}

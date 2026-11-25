package me.bookk.feature.booking.domain.api.entity

import kotlin.uuid.Uuid

data class BookingOverview(
    val id: Uuid,
    val summary: BookingSummary,
    val status: Status,
) {
    sealed interface Status {
        data class Request(
            val status: BookingRequestDetails.Status,
        ) : Status

        data class Appointment(
            val status: BookingAppointmentDetails.Status,
        ) : Status
    }
}

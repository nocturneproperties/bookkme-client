package me.bookk.feature.booking.domain.api.entity

import kotlin.uuid.Uuid

data class BookingAppointmentDetails(
    val id: Uuid,
    val summary: BookingSummary,
    val status: Status,
) {
    enum class Status {
        CONFIRMED,
        COMPLETED,
        CANCELLED,
        MISSED,
    }
}

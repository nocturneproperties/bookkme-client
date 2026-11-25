package me.bookk.feature.booking.domain.api.entity

import kotlin.uuid.Uuid

data class BookingRequestDetails(
    val id: Uuid,
    val summary: BookingSummary,
    val status: Status,
) {
    enum class Status {
        WAITING_FOR_CONFIRMATION,
        CONFIRMED,
        DECLINED,
        CLOSED,
    }
}

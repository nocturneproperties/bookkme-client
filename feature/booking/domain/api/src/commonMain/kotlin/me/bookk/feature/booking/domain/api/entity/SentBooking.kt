package me.bookk.feature.booking.domain.api.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

data class SentBooking(
    val id: Uuid,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val services: List<BookedService>,
    val status: Status,
) {
    data class BookedService(
        val name: String,
        val quantity: Int,
    )

    enum class Status {
        WAITING_FOR_CONFIRMATION,
        CONFIRMED,
    }
}

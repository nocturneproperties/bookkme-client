package me.bookk.feature.booking.domain.api.entity

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class BookingPreparation(
    val services: List<PreparedService>,
    val specialistId: Uuid? = null,
    val slotStart: Instant? = null,
) {
    data class PreparedService(
        val service: BookingService,
        val quantity: Int,
    )
}

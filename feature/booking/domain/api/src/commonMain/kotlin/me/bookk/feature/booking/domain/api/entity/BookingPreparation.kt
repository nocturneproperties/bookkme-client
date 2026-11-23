package me.bookk.feature.booking.domain.api.entity

import kotlin.uuid.Uuid

data class BookingPreparation(
    val services: List<PreparedService>,
    val specialistId: Uuid? = null,
) {
    data class PreparedService(
        val service: BookingService,
        val quantity: Int,
    )
}

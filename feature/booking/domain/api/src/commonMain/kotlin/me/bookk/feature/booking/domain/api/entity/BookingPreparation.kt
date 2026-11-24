package me.bookk.feature.booking.domain.api.entity

import library.money.api.Money
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
        val approvedPrice: Money = service.price,
    )
}

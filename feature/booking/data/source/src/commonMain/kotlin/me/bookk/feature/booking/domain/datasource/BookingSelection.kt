package me.bookk.feature.booking.domain.datasource

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class BookingSelection(
    val services: List<SelectedService> = emptyList(),
    val specialistId: Uuid? = null,
    val slotStart: Instant? = null,
) {
    data class SelectedService(
        val serviceId: Uuid,
        val quantity: Int,
    )
}

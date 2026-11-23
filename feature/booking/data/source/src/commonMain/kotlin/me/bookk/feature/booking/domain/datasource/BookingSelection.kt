package me.bookk.feature.booking.domain.datasource

import kotlin.uuid.Uuid

data class BookingSelection(
    val services: List<SelectedService> = emptyList(),
) {
    data class SelectedService(
        val serviceId: Uuid,
        val quantity: Int,
    )
}

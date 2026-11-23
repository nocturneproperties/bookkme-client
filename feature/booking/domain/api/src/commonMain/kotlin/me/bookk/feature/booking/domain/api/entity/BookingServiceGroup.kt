package me.bookk.feature.booking.domain.api.entity

import kotlin.uuid.Uuid

data class BookingServiceGroup(
    val id: Uuid,
    val name: String,
    val services: List<BookingService>,
)

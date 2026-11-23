package me.bookk.feature.booking.domain.api.entity

import kotlin.uuid.Uuid

data class BookingSpecialist(
    val id: Uuid,
    val name: String,
    val lastName: String,
)

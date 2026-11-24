package me.bookk.feature.booking.presentation.screen

import me.bookk.feature.booking.domain.api.entity.BookingSpecialist

internal fun BookingSpecialist.fullName(): String {
    return listOf(name, lastName)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" ")
}

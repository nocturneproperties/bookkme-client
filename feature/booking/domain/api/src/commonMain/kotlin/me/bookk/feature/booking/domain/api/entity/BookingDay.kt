package me.bookk.feature.booking.domain.api.entity

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Instant

data class BookingDay(
    val date: LocalDate,
    val slots: List<BookingSlot>,
    val isBusinessClosed: Boolean,
)

data class BookingSlot(
    val start: Instant,
    val time: LocalTime,
)

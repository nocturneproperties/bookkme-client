package me.bookk.feature.booking.presentation.screen

import library.money.api.Money
import me.bookk.feature.booking.domain.api.entity.BookingService
import kotlin.time.Duration

internal fun List<BookingService>.totalDuration(): Duration {
    return fold(Duration.ZERO) { total, service -> total + service.duration }
}

internal fun List<BookingService>.totalPrice(): Money {
    return map { it.price }.reduce { total, price -> total + price }
}

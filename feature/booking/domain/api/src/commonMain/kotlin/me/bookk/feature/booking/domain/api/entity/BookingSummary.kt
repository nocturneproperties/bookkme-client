package me.bookk.feature.booking.domain.api.entity

import kotlinx.datetime.LocalDateTime
import library.money.api.Money

data class BookingSummary(
    val businessName: String,
    val specialistName: String,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val note: String,
    val services: List<BookedService>,
    val total: Money,
) {
    data class BookedService(
        val name: String,
        val quantity: Int,
        val price: Money,
    )
}

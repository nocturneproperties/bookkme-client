package me.bookk.feature.booking.domain.api.entity

import library.money.api.Money
import kotlin.time.Duration
import kotlin.uuid.Uuid

data class BookingService(
    val id: Uuid,
    val name: String,
    val duration: Duration,
    val price: Money,
)

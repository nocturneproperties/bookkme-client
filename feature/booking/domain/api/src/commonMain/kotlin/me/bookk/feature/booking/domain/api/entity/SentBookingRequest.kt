package me.bookk.feature.booking.domain.api.entity

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class SentBookingRequest(
    val businessId: Uuid,
    val specialistId: Uuid,
    val startsAt: Instant,
)

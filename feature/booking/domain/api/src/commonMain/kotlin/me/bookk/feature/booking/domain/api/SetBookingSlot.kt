package me.bookk.feature.booking.domain.api

import kotlin.time.Instant
import kotlin.uuid.Uuid

interface SetBookingSlot {
    suspend operator fun invoke(businessId: Uuid, start: Instant)
}

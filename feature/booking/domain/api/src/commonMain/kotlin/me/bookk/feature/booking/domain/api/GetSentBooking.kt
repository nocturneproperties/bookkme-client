package me.bookk.feature.booking.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.booking.domain.api.entity.SentBooking
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest

interface GetSentBooking {
    fun flow(request: SentBookingRequest): Flow<SentBooking?>
}

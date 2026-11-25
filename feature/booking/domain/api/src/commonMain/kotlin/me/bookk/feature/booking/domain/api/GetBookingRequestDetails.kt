package me.bookk.feature.booking.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails
import kotlin.uuid.Uuid

interface GetBookingRequestDetails {
    fun flow(requestId: Uuid): Flow<BookingRequestDetails?>
}

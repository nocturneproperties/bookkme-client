package me.bookk.feature.booking.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import kotlin.uuid.Uuid

interface GetBookingPreparation {
    fun flow(businessId: Uuid): Flow<BookingPreparation>
}

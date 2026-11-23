package me.bookk.feature.booking.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.booking.domain.api.entity.BookingCatalog
import kotlin.uuid.Uuid

interface GetBookingCatalog {
    fun flow(businessId: Uuid): Flow<BookingCatalog>
    suspend fun refresh(businessId: Uuid): BookingCatalog
}

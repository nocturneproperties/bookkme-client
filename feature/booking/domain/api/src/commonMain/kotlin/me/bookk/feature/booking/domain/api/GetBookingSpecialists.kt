package me.bookk.feature.booking.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import kotlin.uuid.Uuid

interface GetBookingSpecialists {
    fun flow(businessId: Uuid): Flow<List<BookingSpecialist>>
    suspend fun refresh(businessId: Uuid): List<BookingSpecialist>
}

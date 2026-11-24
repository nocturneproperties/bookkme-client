package me.bookk.feature.booking.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import kotlin.uuid.Uuid

interface GetBookingTimetable {
    fun flow(businessId: Uuid): Flow<BookingTimetable>
    suspend fun refresh(businessId: Uuid)

    sealed interface Error {
        class BusinessNotFound :
            Throwable(),
            Error
    }
}

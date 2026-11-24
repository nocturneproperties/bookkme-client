package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.BookingInfo
import kotlin.uuid.Uuid

interface GetBookingInfo {
    fun flow(businessId: Uuid): Flow<BookingInfo?>
    suspend fun refresh(businessId: Uuid)

    sealed interface Error {
        class AppointmentsNotEnabled :
            Throwable(),
            Error
    }
}

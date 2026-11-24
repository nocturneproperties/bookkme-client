package me.bookk.feature.booking.domain.api

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface GetBusinessMessage {
    fun flow(businessId: Uuid): Flow<String?>
    suspend fun refresh(businessId: Uuid)

    sealed interface Error {
        class BookingUnavailable :
            Throwable(),
            Error
    }
}

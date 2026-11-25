package me.bookk.feature.booking.domain.api

import kotlin.uuid.Uuid

interface WithdrawBookingRequest {
    suspend operator fun invoke(bookingId: Uuid)

    sealed interface Error {
        class AlreadyConfirmed :
            Throwable(),
            Error
        class NoLongerPending :
            Throwable(),
            Error
    }
}

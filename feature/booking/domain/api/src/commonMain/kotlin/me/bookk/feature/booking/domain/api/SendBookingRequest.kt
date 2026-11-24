package me.bookk.feature.booking.domain.api

import kotlin.uuid.Uuid

interface SendBookingRequest {
    suspend operator fun invoke(businessId: Uuid, note: String)

    sealed interface Error {
        class SelectionIncomplete :
            Throwable(),
            Error
        class TimeUnavailable :
            Throwable(),
            Error
        class PendingRequestsLimitReached :
            Throwable(),
            Error
        class PricesChanged :
            Throwable(),
            Error
        class ServicesChanged :
            Throwable(),
            Error
        class SpecialistUnavailable :
            Throwable(),
            Error
    }
}

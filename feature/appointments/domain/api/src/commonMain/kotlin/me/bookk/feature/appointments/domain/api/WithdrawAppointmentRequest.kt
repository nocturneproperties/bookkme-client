package me.bookk.feature.appointments.domain.api

import kotlin.uuid.Uuid

interface WithdrawAppointmentRequest {
    suspend operator fun invoke(requestId: Uuid)

    sealed interface Error {
        class AlreadyApproved :
            Throwable(),
            Error
        class AlreadyDeclined :
            Throwable(),
            Error
        class NotFound :
            Throwable(),
            Error
    }
}

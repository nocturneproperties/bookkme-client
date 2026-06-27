package me.bookk.feature.appointments.domain.api

import kotlin.uuid.Uuid

interface DeclineAppointmentRequest {
    suspend operator fun invoke(requestId: Uuid, businessId: Uuid, reason: String)

    sealed interface Error {
        class AlreadyDeclined : Throwable(), Error
        class AlreadyApproved : Throwable(), Error
    }
}

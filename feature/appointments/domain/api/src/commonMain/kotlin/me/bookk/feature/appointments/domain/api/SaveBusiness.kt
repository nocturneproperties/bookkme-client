package me.bookk.feature.appointments.domain.api

import kotlin.uuid.Uuid

interface SaveBusiness {
    suspend operator fun invoke(businessId: Uuid)

    sealed interface Error {
        class AppointmentsNotEnabled :
            Throwable(),
            Error
        class SavedBusinessesLimitReached :
            Throwable(),
            Error
    }
}

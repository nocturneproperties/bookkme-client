package me.bookk.feature.employees.domain.api

import kotlin.uuid.Uuid

interface RejectEmployeeInvitation {
    suspend operator fun invoke(businessId: Uuid, id: Uuid)

    sealed interface Error {
        class AlreadyProcessed(cause: Throwable) : Error, Throwable(cause)
    }
}

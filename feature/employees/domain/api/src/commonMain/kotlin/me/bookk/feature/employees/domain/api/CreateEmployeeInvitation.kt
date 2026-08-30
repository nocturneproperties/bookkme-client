package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import kotlin.uuid.Uuid

interface CreateEmployeeInvitation {
    suspend operator fun invoke(businessId: Uuid, email: String): EmployeeInvitation

    sealed interface Error {
        class InvitationExists(cause: Throwable) : Error, Throwable(cause)
        class ValidationError(cause: Throwable) : Error, Throwable(cause)
        class EmployeeExists(cause: Throwable) : Error, Throwable(cause)
    }
}

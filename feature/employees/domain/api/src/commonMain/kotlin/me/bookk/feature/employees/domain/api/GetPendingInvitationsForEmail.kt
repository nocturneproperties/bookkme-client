package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation

interface GetPendingInvitationsForEmail {
    suspend operator fun invoke(email: String): List<EmployeeInvitation>

    sealed interface Error {
        class ValidationError(cause: Throwable) : Error, Throwable(cause)
    }
}

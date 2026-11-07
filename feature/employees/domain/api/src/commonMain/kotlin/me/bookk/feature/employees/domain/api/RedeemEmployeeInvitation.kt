package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.Employee

interface RedeemEmployeeInvitation {
    suspend operator fun invoke(code: String): Employee

    sealed interface Error {
        class AlreadyProcessed(
            cause: Throwable,
        ) : Throwable(cause),
            Error
        class EmployeeExists(
            cause: Throwable,
        ) : Throwable(cause),
            Error
    }
}

package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.Employee

interface RedeemEmployeeInvitation {
    suspend operator fun invoke(code: String): Employee

    sealed interface Error {
        class AlreadyProcessed(cause: Throwable) : Error, Throwable(cause)
        class EmployeeExists(cause: Throwable) : Error, Throwable(cause)
    }
}

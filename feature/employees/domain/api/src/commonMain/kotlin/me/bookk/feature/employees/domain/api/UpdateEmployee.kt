package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.Employee

interface UpdateEmployee {
    suspend operator fun invoke(employee: Employee): Employee

    sealed interface Error {
        class ValidationError(cause: Throwable) : Error, Throwable(cause)
        class ActiveDayWithoutWorkHours(cause: Throwable) : Error, Throwable(cause)
        class InvalidDayOffRange(cause: Throwable) : Error, Throwable(cause)
    }
}

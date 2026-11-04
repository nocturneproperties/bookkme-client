package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.Employee

interface UpdateEmployee {
    suspend operator fun invoke(employee: Employee): Employee

    sealed interface Error {
        class ValidationError(cause: Throwable) :
            Throwable(cause),
            Error
        class ActiveDayWithoutWorkHours(cause: Throwable) :
            Throwable(cause),
            Error
        class InvalidDayOffRange(cause: Throwable) :
            Throwable(cause),
            Error
        class InsufficientGrant(cause: Throwable) :
            Throwable(cause),
            Error
    }
}

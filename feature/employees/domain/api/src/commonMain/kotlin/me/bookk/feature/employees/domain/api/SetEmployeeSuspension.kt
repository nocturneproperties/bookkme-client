package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.Employee
import kotlin.uuid.Uuid

interface SetEmployeeSuspension {
    suspend operator fun invoke(businessId: Uuid, id: Uuid, suspended: Boolean): Employee

    sealed interface Error {
        class OwnerSuspensionNotAllowed(cause: Throwable) : Error, Throwable(cause)
    }
}

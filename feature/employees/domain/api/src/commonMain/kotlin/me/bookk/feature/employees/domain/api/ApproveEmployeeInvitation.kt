package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.Employee
import kotlin.uuid.Uuid

interface ApproveEmployeeInvitation {
    suspend operator fun invoke(businessId: Uuid, id: Uuid): Employee

    sealed interface Error {
        class AlreadyProcessed(cause: Throwable) : Error, Throwable(cause)
        class EmployeeExists(cause: Throwable) : Error, Throwable(cause)
    }
}

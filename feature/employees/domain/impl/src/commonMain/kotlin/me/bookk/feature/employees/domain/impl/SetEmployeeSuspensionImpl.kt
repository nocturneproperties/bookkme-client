package me.bookk.feature.employees.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.employees.domain.api.SetEmployeeSuspension
import me.bookk.feature.employees.domain.api.SetEmployeeSuspension.Error
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import kotlin.uuid.Uuid

internal class SetEmployeeSuspensionImpl(
    private val dataSource: EmployeeDataSource
) : SetEmployeeSuspension {
    override suspend fun invoke(businessId: Uuid, id: Uuid, suspended: Boolean): Employee {
        return runCatching {
            dataSource.setEmployeeSuspension(businessId, id, suspended)
        }.onBusinessError { error ->
            when (error.errorCode) {
                EmployeeErrorCodes.BUSINESS_OWNER_SUSPENSION_NOT_ALLOWED -> throw Error.OwnerSuspensionNotAllowed(error)
            }
        }.getOrThrow().also {
            dataSource.saveEmployeesInDb(listOf(it))
        }
    }
}

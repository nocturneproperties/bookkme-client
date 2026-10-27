package me.bookk.feature.employees.domain.impl

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.employees.domain.api.IsBusinessOwner
import me.bookk.feature.employees.domain.api.UpdateEmployee
import me.bookk.feature.employees.domain.api.UpdateEmployee.Error
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes

internal class UpdateEmployeeImpl(
    private val dataSource: EmployeeDataSource,
    private val isBusinessOwner: IsBusinessOwner
) : UpdateEmployee {
    override suspend fun invoke(employee: Employee): Employee {
        return runCatching {
            if (canUpdatePermissions(employee)) {
                sendUpdates(employee)
            } else {
                dataSource.updateEmployee(employee)
            }
        }.onBusinessError { error ->
            when (error.errorCode) {
                EmployeeErrorCodes.BUSINESS_EMPLOYEE_VALIDATION_ERROR -> throw Error.ValidationError(error)
                EmployeeErrorCodes.BUSINESS_EMPLOYEE_ACTIVE_DAY_WITHOUT_WORK_HOURS -> throw Error.ActiveDayWithoutWorkHours(error)
                EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVALID_DAY_OFF_RANGE -> throw Error.InvalidDayOffRange(error)
                EmployeeErrorCodes.BUSINESS_INSUFFICIENT_GRANT_PERMISSION -> throw Error.InsufficientGrant(error)
            }
        }.getOrThrow().also {
            dataSource.saveEmployeesInDb(listOf(it))
        }
    }

    private suspend fun canUpdatePermissions(employee: Employee): Boolean {
        return isBusinessOwner(employee.businessId) && !isBusinessOwner(employee.userId, employee.businessId)
    }

    private suspend fun sendUpdates(employee: Employee) = coroutineScope {
        val profile = async { dataSource.updateEmployee(employee) }
        val permissions = async {
            dataSource.updateEmployeePermissions(employee.businessId, employee.id, employee.permissions)
        }
        profile.await().copy(permissions = permissions.await().permissions)
    }
}

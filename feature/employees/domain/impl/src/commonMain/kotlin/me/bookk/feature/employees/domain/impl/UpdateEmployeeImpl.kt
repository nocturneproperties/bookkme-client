package me.bookk.feature.employees.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.employees.domain.api.UpdateEmployee
import me.bookk.feature.employees.domain.api.UpdateEmployee.Error
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes

internal class UpdateEmployeeImpl(
    private val dataSource: EmployeeDataSource
) : UpdateEmployee {
    override suspend fun invoke(employee: Employee): Employee = runCatching {
        dataSource.updateEmployee(employee)
    }.onBusinessError { error ->
        when (error.errorCode) {
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_VALIDATION_ERROR -> throw Error.ValidationError(error)
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_ACTIVE_DAY_WITHOUT_WORK_HOURS -> throw Error.ActiveDayWithoutWorkHours(error)
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVALID_DAY_OFF_RANGE -> throw Error.InvalidDayOffRange(error)
        }
    }.getOrThrow()
}

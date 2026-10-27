package me.bookk.feature.employees.domain.impl

import me.bookk.feature.employees.domain.api.GetEmployee
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.uuid.Uuid

internal class GetEmployeeImpl(
    private val dataSource: EmployeeDataSource
) : GetEmployee {
    override suspend fun invoke(id: Uuid): Employee {
        return dataSource.getEmployeeFromDb(id) ?: throw GetEmployee.Error.NotFound()
    }
}

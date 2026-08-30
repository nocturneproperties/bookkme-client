package me.bookk.feature.employees.domain.impl

import me.bookk.feature.employees.domain.api.GetEmployees
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.uuid.Uuid

internal class GetEmployeesImpl(
    private val dataSource: EmployeeDataSource
) : GetEmployees {
    override suspend fun invoke(businessId: Uuid): List<Employee> {
        return dataSource.getEmployees(businessId).also {
            dataSource.deleteEmployeesInDb()
            dataSource.saveEmployeesInDb(it)
            dataSource.saveLastSyncedAt(businessId)
        }
    }

    override suspend fun cached(
        businessId: Uuid,
        onResultAvailable: suspend (List<Employee>) -> Unit
    ) {
        if (dataSource.getLastSyncedAt(businessId) != null) {
            onResultAvailable(dataSource.getEmployeesFromDb(businessId))
        }
        onResultAvailable(invoke(businessId))
    }
}

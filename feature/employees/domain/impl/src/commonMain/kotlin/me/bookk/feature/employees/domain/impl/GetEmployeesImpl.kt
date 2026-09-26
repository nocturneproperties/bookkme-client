package me.bookk.feature.employees.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.employees.domain.api.GetEmployees
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.uuid.Uuid

internal class GetEmployeesImpl(
    private val dataSource: EmployeeDataSource,
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges
) : GetEmployees {

    override fun flow(): Flow<List<Employee>> {
        return observeDashboardBusinessChanges()
            .flatMapLatestOrNull { business -> dataSource.observeEmployeesDBChanges(business.id) }
            .map { it.orEmpty() }
    }

    override suspend fun refresh(businessId: Uuid): List<Employee> {
        val employees = dataSource.getEmployees(businessId)
        val freshIds = employees.map { it.id }.toSet()
        val staleIds = dataSource.getEmployeeIdsInDb(businessId).filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            dataSource.deleteEmployeesInDb(staleIds)
        }
        dataSource.saveEmployeesInDb(employees)
        dataSource.saveLastSyncedAt(businessId)
        return employees
    }
}

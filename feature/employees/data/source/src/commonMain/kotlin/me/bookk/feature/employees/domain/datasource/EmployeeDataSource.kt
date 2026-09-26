package me.bookk.feature.employees.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.employees.domain.api.entity.Employee
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface EmployeeDataSource {
    suspend fun getEmployees(businessId: Uuid): List<Employee>
    fun observeEmployeesDBChanges(businessId: Uuid): Flow<List<Employee>>
    suspend fun getEmployeeFromDb(id: Uuid): Employee?
    suspend fun saveEmployeesInDb(employees: List<Employee>)
    suspend fun getEmployeeIdsInDb(businessId: Uuid): List<Uuid>
    suspend fun deleteEmployeesInDb(ids: List<Uuid>)
    suspend fun getLastSyncedAt(businessId: Uuid): Instant?
    suspend fun saveLastSyncedAt(businessId: Uuid)
    suspend fun updateEmployee(employee: Employee): Employee
    suspend fun updateEmployeePermissions(businessId: Uuid, id: Uuid, permissions: BusinessPermissions): Employee
}

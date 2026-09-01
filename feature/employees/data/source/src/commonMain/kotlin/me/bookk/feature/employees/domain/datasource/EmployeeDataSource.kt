package me.bookk.feature.employees.domain.datasource

import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeRole
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface EmployeeDataSource {
    suspend fun getEmployees(businessId: Uuid): List<Employee>
    suspend fun getEmployeesFromDb(businessId: Uuid): List<Employee>
    suspend fun saveEmployeesInDb(employees: List<Employee>)
    suspend fun deleteEmployeesInDb()
    suspend fun getLastSyncedAt(businessId: Uuid): Instant?
    suspend fun saveLastSyncedAt(businessId: Uuid)
    suspend fun updateEmployee(employee: Employee): Employee
    suspend fun promoteEmployee(businessId: Uuid, id: Uuid, role: EmployeeRole)
}

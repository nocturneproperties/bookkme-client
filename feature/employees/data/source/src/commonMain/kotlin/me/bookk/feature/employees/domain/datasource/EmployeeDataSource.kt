package me.bookk.feature.employees.domain.datasource

import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeRole
import kotlin.uuid.Uuid

interface EmployeeDataSource {
    suspend fun getEmployees(businessId: Uuid): List<Employee>
    suspend fun updateEmployee(employee: Employee): Employee
    suspend fun promoteEmployee(businessId: Uuid, id: Uuid, role: EmployeeRole)
}

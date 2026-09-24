package me.bookk.feature.employees.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.BusinessResource
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeRole
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface EmployeeDataSource {
    suspend fun getEmployees(businessId: Uuid): List<Employee>
    fun observeEmployeesDBChanges(businessId: Uuid): Flow<List<Employee>>
    suspend fun saveEmployeesInDb(employees: List<Employee>)
    suspend fun getEmployeeIdsInDb(businessId: Uuid): List<Uuid>
    suspend fun deleteEmployeesInDb(ids: List<Uuid>)
    suspend fun getLastSyncedAt(businessId: Uuid): Instant?
    suspend fun saveLastSyncedAt(businessId: Uuid)
    suspend fun updateEmployee(employee: Employee): Employee
    suspend fun promoteEmployee(businessId: Uuid, id: Uuid, role: EmployeeRole)
    suspend fun getEmployeePermissions(businessId: Uuid, id: Uuid): BusinessPermissions
    suspend fun setEmployeePermission(
        businessId: Uuid,
        id: Uuid,
        resource: BusinessResource,
        permission: ResourcePermission
    ): BusinessPermissions
}

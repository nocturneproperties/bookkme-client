package me.bookk.feature.employees.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.EmployeeDao
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.BusinessResource
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.employees.data.mapping.toDayOffEntities
import me.bookk.feature.employees.data.mapping.toDayScheduleEntities
import me.bookk.feature.employees.data.mapping.toDomain
import me.bookk.feature.employees.data.mapping.toEntity
import me.bookk.feature.employees.data.mapping.toServiceSnapshotEntities
import me.bookk.feature.employees.data.mapping.toWorkHourEntities
import me.bookk.feature.employees.data.remote.api.EmployeeRouting.Api
import me.bookk.feature.employees.data.remote.model.BusinessPermissionsRemote
import me.bookk.feature.employees.data.remote.model.EmployeeRemote
import me.bookk.feature.employees.data.remote.model.EmployeeRoleRemote
import me.bookk.feature.employees.data.remote.model.PromoteEmployeeRequestRemote
import me.bookk.feature.employees.data.remote.model.ResourcePermissionRemote
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeRole
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal class EmployeeDataSourceImpl(
    private val httpClient: HttpClient,
    private val employeeDao: EmployeeDao,
    preferenceProvider: PreferenceProvider
) : DataSource(), EmployeeDataSource, LogOutAction {

    private val preferences = preferenceProvider.get("employees_prefs")
    override suspend fun getEmployees(businessId: Uuid): List<Employee> = mapExceptions {
        httpClient.get(Api.Employee(businessId = businessId))
            .body<List<EmployeeRemote>>()
            .map { it.toDomain() }
    }

    override suspend fun getEmployeesFromDb(businessId: Uuid): List<Employee> = mapExceptions {
        employeeDao.getEmployees(businessId).map { it.toDomain() }
    }

    override suspend fun saveEmployeesInDb(employees: List<Employee>) {
        mapExceptions {
            employeeDao.upsertAllWithChildren(
                employees = employees.map(Employee::toEntity),
                daySchedules = employees.flatMap(Employee::toDayScheduleEntities),
                workHours = employees.flatMap(Employee::toWorkHourEntities),
                dayOffs = employees.flatMap(Employee::toDayOffEntities),
                services = employees.flatMap(Employee::toServiceSnapshotEntities)
            )
        }
    }

    override suspend fun deleteEmployeesInDb() {
        mapExceptions { employeeDao.clear() }
    }

    override suspend fun getLastSyncedAt(businessId: Uuid): Instant? {
        return preferences.get(Key.lastSyncedAt(businessId))?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun saveLastSyncedAt(businessId: Uuid) {
        preferences.set(Key.lastSyncedAt(businessId), Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
        employeeDao.clear()
    }

    private object Key {
        fun lastSyncedAt(businessId: Uuid) = Preferences.Key<Long>("last_synced_at_$businessId")
    }

    override suspend fun updateEmployee(employee: Employee): Employee = mapExceptions {
        httpClient.put(Api.Employee.Id(Api.Employee(businessId = employee.businessId), employee.id)) {
            setBody(EmployeeRemote.fromDomain(employee))
        }
            .body<EmployeeRemote>()
            .toDomain()
    }

    override suspend fun promoteEmployee(businessId: Uuid, id: Uuid, role: EmployeeRole) {
        mapExceptions {
            httpClient.post(Api.Employee.Id.Promote(Api.Employee.Id(Api.Employee(businessId = businessId), id))) {
                setBody(PromoteEmployeeRequestRemote(role = EmployeeRoleRemote.fromDomain(role)))
            }
        }
    }

    override suspend fun getEmployeePermissions(businessId: Uuid, id: Uuid): BusinessPermissions = mapExceptions {
        val employeeId = Api.Employee.Id(Api.Employee(businessId = businessId), id)
        httpClient.get(Api.Employee.Id.Permissions(employeeId))
            .body<BusinessPermissionsRemote>()
            .toDomain()
    }

    override suspend fun setEmployeePermission(
        businessId: Uuid,
        id: Uuid,
        resource: BusinessResource,
        permission: ResourcePermission
    ): BusinessPermissions = mapExceptions {
        val employeeId = Api.Employee.Id(Api.Employee(businessId = businessId), id)
        val permissions = Api.Employee.Id.Permissions(employeeId)
        httpClient.put(Api.Employee.Id.Permissions.Grant(permissions, resource.wireValue)) {
            setBody(ResourcePermissionRemote.fromDomain(permission))
        }
            .body<BusinessPermissionsRemote>()
            .toDomain()
    }

    private val BusinessResource.wireValue: String
        get() = name.lowercase()
}

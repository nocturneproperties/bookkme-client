package me.bookk.feature.employees.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.EmployeeDao
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.employees.data.mapping.toDayOffEntities
import me.bookk.feature.employees.data.mapping.toDayScheduleEntities
import me.bookk.feature.employees.data.mapping.toDomain
import me.bookk.feature.employees.data.mapping.toEntity
import me.bookk.feature.employees.data.mapping.toServiceSnapshotEntities
import me.bookk.feature.employees.data.mapping.toWorkHourEntities
import me.bookk.feature.employees.data.remote.api.EmployeeRouting.Api
import me.bookk.feature.employees.data.remote.model.EmployeePermissionsRequest
import me.bookk.feature.employees.data.remote.model.EmployeeRemote
import me.bookk.feature.employees.data.remote.model.EmployeeSuspensionRequest
import me.bookk.feature.employees.data.remote.model.EmployeeUpdateRequest
import me.bookk.feature.employees.domain.api.entity.Employee
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

    override fun observeEmployeesDBChanges(businessId: Uuid): Flow<List<Employee>> =
        employeeDao.observeEmployees(businessId)
            .map { employees -> employees.map { it.toDomain() } }
            .mapErrors()

    override suspend fun getEmployeeFromDb(id: Uuid): Employee? = mapExceptions {
        employeeDao.getEmployee(id)?.toDomain()
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

    override suspend fun getEmployeeIdsInDb(businessId: Uuid): List<Uuid> = mapExceptions {
        employeeDao.getIdsForBusiness(businessId)
    }

    override suspend fun deleteEmployeesInDb(ids: List<Uuid>) {
        mapExceptions {
            ids.chunked(DELETE_CHUNK_SIZE).forEach { chunk -> employeeDao.deleteByIds(chunk) }
        }
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
            setBody(EmployeeUpdateRequest.fromDomain(employee))
        }
            .body<EmployeeRemote>()
            .toDomain()
    }

    override suspend fun updateEmployeePermissions(
        businessId: Uuid,
        id: Uuid,
        permissions: BusinessPermissions
    ): Employee = mapExceptions {
        val employeeId = Api.Employee.Id(Api.Employee(businessId = businessId), id)
        httpClient.put(Api.Employee.Id.Permissions(employeeId)) {
            setBody(EmployeePermissionsRequest.fromDomain(permissions))
        }
            .body<EmployeeRemote>()
            .toDomain()
    }

    override suspend fun setEmployeeSuspension(businessId: Uuid, id: Uuid, suspended: Boolean): Employee = mapExceptions {
        val employeeId = Api.Employee.Id(Api.Employee(businessId = businessId), id)
        httpClient.put(Api.Employee.Id.Suspension(employeeId)) {
            setBody(EmployeeSuspensionRequest(suspended = suspended))
        }
            .body<EmployeeRemote>()
            .toDomain()
    }
}

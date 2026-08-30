package me.bookk.feature.employees.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.feature.employees.data.remote.api.EmployeeRouting.Api
import me.bookk.feature.employees.data.remote.model.EmployeeRemote
import me.bookk.feature.employees.data.remote.model.EmployeeRoleRemote
import me.bookk.feature.employees.data.remote.model.PromoteEmployeeRequestRemote
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeRole
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.uuid.Uuid

internal class EmployeeDataSourceImpl(
    private val httpClient: HttpClient
) : DataSource(), EmployeeDataSource {
    override suspend fun getEmployees(businessId: Uuid): List<Employee> = mapExceptions {
        httpClient.get(Api.Employee(businessId = businessId))
            .body<List<EmployeeRemote>>()
            .map { it.toDomain() }
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
}

package me.bookk.feature.employees.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.feature.employees.data.remote.api.EmployeeRouting.Api
import me.bookk.feature.employees.data.remote.model.EmployeeInvitationRemote
import me.bookk.feature.employees.data.remote.model.EmployeeInvitationRequestRemote
import me.bookk.feature.employees.data.remote.model.EmployeeRemote
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.uuid.Uuid

internal class EmployeeInvitationDataSourceImpl(
    private val httpClient: HttpClient
) : DataSource(), EmployeeInvitationDataSource {
    override suspend fun createInvitation(businessId: Uuid, email: String): EmployeeInvitation = mapExceptions {
        httpClient.post(Api.EmployeeInvitation(businessId = businessId)) {
            setBody(EmployeeInvitationRequestRemote(email = email))
        }
            .body<EmployeeInvitationRemote>()
            .toDomain()
    }

    override suspend fun getInvitations(businessId: Uuid): List<EmployeeInvitation> = mapExceptions {
        httpClient.get(Api.EmployeeInvitation(businessId = businessId))
            .body<List<EmployeeInvitationRemote>>()
            .map { it.toDomain() }
    }

    override suspend fun approveInvitation(businessId: Uuid, id: Uuid): Employee = mapExceptions {
        httpClient.post(Api.EmployeeInvitation.Approve(Api.EmployeeInvitation(businessId = businessId), id))
            .body<EmployeeRemote>()
            .toDomain()
    }
}

package me.bookk.feature.employees.domain.datasource

import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import kotlin.uuid.Uuid

interface EmployeeInvitationDataSource {
    suspend fun createInvitation(businessId: Uuid, email: String): EmployeeInvitation
    suspend fun getInvitations(businessId: Uuid): List<EmployeeInvitation>
    suspend fun approveInvitation(businessId: Uuid, id: Uuid): Employee
}

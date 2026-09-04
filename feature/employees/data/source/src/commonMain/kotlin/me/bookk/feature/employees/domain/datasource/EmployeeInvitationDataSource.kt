package me.bookk.feature.employees.domain.datasource

import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface EmployeeInvitationDataSource {
    suspend fun createInvitation(businessId: Uuid): EmployeeInvitation
    suspend fun getInvitations(businessId: Uuid): List<EmployeeInvitation>
    suspend fun getInvitationsFromDb(businessId: Uuid): List<EmployeeInvitation>
    suspend fun saveInvitationsInDb(invitations: List<EmployeeInvitation>)
    suspend fun deleteInvitationsInDb()
    suspend fun getLastSyncedAt(businessId: Uuid): Instant?
    suspend fun saveLastSyncedAt(businessId: Uuid)
    suspend fun revokeInvitation(businessId: Uuid, id: Uuid)
    suspend fun redeemInvitation(code: String): Employee
}

package me.bookk.feature.employees.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.employees.domain.api.GetEmployeeInvitations
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.uuid.Uuid

internal class GetEmployeeInvitationsImpl(
    private val dataSource: EmployeeInvitationDataSource
) : GetEmployeeInvitations {

    override fun flow(businessId: Uuid): Flow<List<EmployeeInvitation>> =
        dataSource.observeInvitationsDBChanges(businessId)

    override suspend fun refresh(businessId: Uuid): List<EmployeeInvitation> {
        val invitations = dataSource.getInvitations(businessId)
        val freshIds = invitations.map { it.id }.toSet()
        val staleIds = dataSource.getInvitationIdsInDb(businessId).filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            dataSource.deleteInvitationsInDb(staleIds)
        }
        dataSource.saveInvitationsInDb(invitations)
        dataSource.saveLastSyncedAt(businessId)
        return invitations
    }
}

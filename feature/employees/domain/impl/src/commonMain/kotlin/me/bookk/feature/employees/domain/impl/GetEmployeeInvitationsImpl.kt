package me.bookk.feature.employees.domain.impl

import me.bookk.feature.employees.domain.api.GetEmployeeInvitations
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.uuid.Uuid

internal class GetEmployeeInvitationsImpl(
    private val dataSource: EmployeeInvitationDataSource
) : GetEmployeeInvitations {
    override suspend fun invoke(businessId: Uuid): List<EmployeeInvitation> {
        return dataSource.getInvitations(businessId).also {
            dataSource.deleteInvitationsInDb()
            dataSource.saveInvitationsInDb(it)
            dataSource.saveLastSyncedAt(businessId)
        }
    }

    override suspend fun cached(
        businessId: Uuid,
        onResultAvailable: suspend (List<EmployeeInvitation>) -> Unit
    ) {
        if (dataSource.getLastSyncedAt(businessId) != null) {
            onResultAvailable(dataSource.getInvitationsFromDb(businessId))
        }
        onResultAvailable(invoke(businessId))
    }
}

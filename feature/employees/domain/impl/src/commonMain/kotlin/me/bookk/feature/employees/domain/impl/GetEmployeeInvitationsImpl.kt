package me.bookk.feature.employees.domain.impl

import me.bookk.feature.employees.domain.api.GetEmployeeInvitations
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.uuid.Uuid

internal class GetEmployeeInvitationsImpl(
    private val dataSource: EmployeeInvitationDataSource
) : GetEmployeeInvitations {
    override suspend fun invoke(businessId: Uuid): List<EmployeeInvitation> {
        return dataSource.getInvitations(businessId)
    }
}

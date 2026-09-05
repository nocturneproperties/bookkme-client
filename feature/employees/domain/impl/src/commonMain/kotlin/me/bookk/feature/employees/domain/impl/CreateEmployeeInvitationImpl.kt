package me.bookk.feature.employees.domain.impl

import me.bookk.feature.employees.domain.api.CreateEmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.uuid.Uuid

internal class CreateEmployeeInvitationImpl(
    private val dataSource: EmployeeInvitationDataSource
) : CreateEmployeeInvitation {
    override suspend fun invoke(businessId: Uuid): EmployeeInvitation {
        return dataSource.createInvitation(businessId)
    }
}

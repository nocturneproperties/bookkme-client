package me.bookk.feature.employees.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.employees.domain.api.ApproveEmployeeInvitation
import me.bookk.feature.employees.domain.api.ApproveEmployeeInvitation.Error
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.uuid.Uuid

internal class ApproveEmployeeInvitationImpl(
    private val dataSource: EmployeeInvitationDataSource
) : ApproveEmployeeInvitation {
    override suspend fun invoke(businessId: Uuid, id: Uuid): Employee = runCatching {
        dataSource.approveInvitation(businessId, id)
    }.onBusinessError { error ->
        when (error.errorCode) {
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_ALREADY_PROCESSED -> throw Error.AlreadyProcessed(error)
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_EXISTS -> throw Error.EmployeeExists(error)
        }
    }.getOrThrow()
}

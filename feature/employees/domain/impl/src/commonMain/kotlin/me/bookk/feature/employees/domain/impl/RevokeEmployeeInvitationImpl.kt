package me.bookk.feature.employees.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.employees.domain.api.RevokeEmployeeInvitation
import me.bookk.feature.employees.domain.api.RevokeEmployeeInvitation.Error
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.uuid.Uuid

internal class RevokeEmployeeInvitationImpl(
    private val dataSource: EmployeeInvitationDataSource
) : RevokeEmployeeInvitation {
    override suspend fun invoke(businessId: Uuid, id: Uuid) = runCatching {
        dataSource.revokeInvitation(businessId, id)
    }.onBusinessError { error ->
        when (error.errorCode) {
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_ALREADY_PROCESSED -> throw Error.AlreadyProcessed(error)
        }
    }.getOrThrow()
}

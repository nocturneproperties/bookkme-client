package me.bookk.feature.employees.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.employees.domain.api.CreateEmployeeInvitation
import me.bookk.feature.employees.domain.api.CreateEmployeeInvitation.Error
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.uuid.Uuid

internal class CreateEmployeeInvitationImpl(
    private val dataSource: EmployeeInvitationDataSource
) : CreateEmployeeInvitation {
    override suspend fun invoke(businessId: Uuid, email: String): EmployeeInvitation = runCatching {
        dataSource.createInvitation(businessId, email)
    }.onBusinessError { error ->
        when (error.errorCode) {
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_EXISTS -> throw Error.InvitationExists(error)
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_VALIDATION_ERROR -> throw Error.ValidationError(error)
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_EXISTS -> throw Error.EmployeeExists(error)
        }
    }.getOrThrow()
}

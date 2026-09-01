package me.bookk.feature.employees.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.employees.domain.api.GetPendingInvitationsForEmail
import me.bookk.feature.employees.domain.api.GetPendingInvitationsForEmail.Error
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource

internal class GetPendingInvitationsForEmailImpl(
    private val dataSource: EmployeeInvitationDataSource
) : GetPendingInvitationsForEmail {
    override suspend fun invoke(email: String): List<EmployeeInvitation> = runCatching {
        dataSource.getPendingInvitationsForEmail(email)
    }.onBusinessError { error ->
        when (error.errorCode) {
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_VALIDATION_ERROR -> throw Error.ValidationError(error)
        }
    }.getOrThrow()
}

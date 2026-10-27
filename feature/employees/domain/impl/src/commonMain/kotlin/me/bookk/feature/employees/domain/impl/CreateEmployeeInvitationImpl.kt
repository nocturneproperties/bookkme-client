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
    override suspend fun invoke(businessId: Uuid): EmployeeInvitation {
        return runCatching {
            dataSource.createInvitation(businessId)
        }.onBusinessError { error ->
            when (error.errorCode) {
                EmployeeErrorCodes.BUSINESS_EMPLOYEE_PENDING_INVITATIONS_LIMIT_REACHED ->
                    throw Error.PendingInvitationsLimitReached(error)

                EmployeeErrorCodes.BUSINESS_EMPLOYEE_DAILY_INVITATIONS_LIMIT_REACHED ->
                    throw Error.DailyInvitationsLimitReached(error)
            }
        }.getOrThrow()
    }
}

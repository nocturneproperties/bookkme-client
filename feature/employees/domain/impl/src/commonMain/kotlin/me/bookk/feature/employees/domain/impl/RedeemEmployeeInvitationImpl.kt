package me.bookk.feature.employees.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.employees.domain.api.RedeemEmployeeInvitation
import me.bookk.feature.employees.domain.api.RedeemEmployeeInvitation.Error
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource

internal class RedeemEmployeeInvitationImpl(
    private val dataSource: EmployeeInvitationDataSource
) : RedeemEmployeeInvitation {
    override suspend fun invoke(code: String): Employee = runCatching {
        dataSource.redeemInvitation(code)
    }.onBusinessError { error ->
        when (error.errorCode) {
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_INVITATION_ALREADY_PROCESSED -> throw Error.AlreadyProcessed(error)
            EmployeeErrorCodes.BUSINESS_EMPLOYEE_EXISTS -> throw Error.EmployeeExists(error)
        }
    }.getOrThrow()
}

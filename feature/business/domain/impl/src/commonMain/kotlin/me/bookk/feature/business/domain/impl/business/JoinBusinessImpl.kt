package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.JoinBusiness
import me.bookk.feature.employees.domain.api.RedeemEmployeeInvitation

internal class JoinBusinessImpl(
    private val redeemEmployeeInvitation: RedeemEmployeeInvitation
) : JoinBusiness {
    override suspend fun invoke(code: String) {
        try {
            redeemEmployeeInvitation(code)
        } catch (e: RedeemEmployeeInvitation.Error.AlreadyProcessed) {
            throw JoinBusiness.Error.AlreadyProcessed(e)
        } catch (e: RedeemEmployeeInvitation.Error.EmployeeExists) {
            throw JoinBusiness.Error.EmployeeExists(e)
        }
    }
}

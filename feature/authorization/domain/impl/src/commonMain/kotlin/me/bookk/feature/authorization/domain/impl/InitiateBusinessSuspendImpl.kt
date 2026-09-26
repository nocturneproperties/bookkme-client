package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.InitiateBusinessSuspend
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness

internal class InitiateBusinessSuspendImpl(
    private val switchDashboardBusiness: SwitchDashboardBusiness,
    private val refreshBusinessInfo: RefreshBusinessInfo
) : InitiateBusinessSuspend {
    override suspend fun invoke() {
        switchDashboardBusiness(null)
        refreshBusinessInfo(applyDashboardIdFromRemote = false)
    }
}

package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.uuid.Uuid

internal class SwitchDashboardBusinessImpl(
    private val businessDataSource: BusinessDataSource
) : SwitchDashboardBusiness {
    override suspend fun invoke(businessId: Uuid) {
        businessDataSource.saveDashboardBusinessId(businessId)
        runCatching { businessDataSource.setDashboardBusinessOnRemote(businessId) }
    }
}

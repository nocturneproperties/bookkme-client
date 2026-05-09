package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class RefreshBusinessInfoImpl(
    private val businessDataSource: BusinessDataSource
) : RefreshBusinessInfo {
    override suspend fun invoke() {
        val data = businessDataSource.getBusinessesFromRemote()
        businessDataSource.saveDashboardBusinessId(data.dashboardId)
        businessDataSource.saveBusinessListInDB(data.businesses)
    }
}
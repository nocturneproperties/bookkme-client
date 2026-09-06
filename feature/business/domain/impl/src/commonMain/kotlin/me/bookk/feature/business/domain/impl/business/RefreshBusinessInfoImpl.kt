package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class RefreshBusinessInfoImpl(
    private val businessDataSource: BusinessDataSource,
    private val isAppointmentsPluginEnabled: IsAppointmentsPluginEnabled
) : RefreshBusinessInfo {
    override suspend fun invoke(applyDashboardIdFromRemote: Boolean) {
        val data = businessDataSource.getBusinessesFromRemote()
        if (applyDashboardIdFromRemote) {
            businessDataSource.saveDashboardBusinessId(data.dashboardId)
        }
        businessDataSource.saveBusinessListInDB(data.businesses)
        data.businesses.forEach { business ->
            runCatching { isAppointmentsPluginEnabled(business.id) }
        }
    }
}
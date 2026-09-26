package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo
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
        deleteRemovedBusinesses(data)
        businessDataSource.saveBusinessListInDB(data.businesses)
        data.businesses.forEach { business ->
            runCatching { isAppointmentsPluginEnabled.refresh(business.id) }
        }
    }

    private suspend fun deleteRemovedBusinesses(data: UserBusinessInfo) {
        val remoteIds = data.businesses.map { it.id }.toSet()
        val removedIds = businessDataSource.getBusinessIdsInDb().filterNot { it in remoteIds }
        if (removedIds.isNotEmpty()) {
            businessDataSource.deleteBusinessesInDb(removedIds)
        }
    }
}

package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.uuid.Uuid

internal class RefreshBusinessInfoImpl(
    private val businessDataSource: BusinessDataSource,
    private val isAppointmentsPluginEnabled: IsAppointmentsPluginEnabled
) : RefreshBusinessInfo {
    override suspend fun invoke(applyDashboardIdFromRemote: Boolean) {
        val data = businessDataSource.getBusinessesFromRemote()
        deleteRemovedBusinesses(data)
        if (applyDashboardIdFromRemote) {
            businessDataSource.saveBusinessListWithDashboardInDB(data.businesses, data.dashboardId?.takeIf { data.contains(it) })
        } else {
            businessDataSource.saveBusinessListInDB(data.businesses)
        }
        refreshPluginStates(data)
    }

    override suspend fun refreshAndSelect(businessId: Uuid) {
        val data = businessDataSource.getBusinessesFromRemote()
        deleteRemovedBusinesses(data)
        if (data.contains(businessId)) {
            businessDataSource.saveBusinessListWithDashboardInDB(data.businesses, businessId)
        } else {
            businessDataSource.saveBusinessListInDB(data.businesses)
        }
        refreshPluginStates(data)
    }

    private suspend fun deleteRemovedBusinesses(data: UserBusinessInfo) {
        val remoteIds = data.businesses.map { it.id }.toSet()
        val removedIds = businessDataSource.getBusinessIdsInDb().filterNot { it in remoteIds }
        if (removedIds.isNotEmpty()) {
            businessDataSource.deleteBusinessesInDb(removedIds)
        }
    }

    private suspend fun refreshPluginStates(data: UserBusinessInfo) {
        data.businesses.forEach { business ->
            runCatching { isAppointmentsPluginEnabled.refresh(business.id) }
        }
    }

    private fun UserBusinessInfo.contains(businessId: Uuid): Boolean {
        return businesses.any { it.id == businessId }
    }
}

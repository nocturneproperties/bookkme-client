package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.uuid.Uuid

internal class GetAvailableDashboardFeaturesImpl(
    private val isAppointmentsPluginEnabled: IsAppointmentsPluginEnabled,
    private val businessDataSource: BusinessDataSource
) : GetAvailableDashboardFeatures {
    override suspend fun invoke(): Set<DashboardFeature> {
        val businessId = businessDataSource.getDashboardBusinessId() ?: return emptySet()
        val business = businessDataSource.getBusinessById(businessId) ?: return emptySet()

        return buildSet {
            addResourceFeatures(business)

            val isAppointmentsEnabled = runCatching {
                isAppointmentsPluginEnabled(business.id)
            }.getOrElse { false }

            if (isAppointmentsEnabled && business.permissions.appointments.view) {
                add(DashboardFeature.APPOINTMENTS)
            }
        }.also { businessDataSource.saveDashboardFeatures(businessId, it) }
    }

    override suspend fun cached(businessId: Uuid, onResultAvailable: suspend (Set<DashboardFeature>) -> Unit) {
        businessDataSource.getDashboardFeatures(businessId)?.let { onResultAvailable(it) }
        onResultAvailable(invoke())
    }

    private fun MutableSet<DashboardFeature>.addResourceFeatures(business: Business) {
        val permissions = business.permissions
        if (permissions.business.view) add(DashboardFeature.BUSINESS)
        if (permissions.employees.view) add(DashboardFeature.EMPLOYEES)
        if (permissions.clients.view) add(DashboardFeature.CLIENTS)
        if (permissions.services.view) add(DashboardFeature.SERVICES)
    }
}

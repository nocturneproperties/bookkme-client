package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class GetAvailableDashboardFeaturesImpl(
    private val isAppointmentsPluginEnabled: IsAppointmentsPluginEnabled,
    private val businessDataSource: BusinessDataSource
) : GetAvailableDashboardFeatures {
    override suspend fun invoke(): Set<DashboardFeature> {
        return buildSet {
            val business = businessDataSource.getDashboardBusinessId()?.let {
                businessDataSource.getBusinessById(it)
            } ?: return@buildSet
            add(DashboardFeature.BUSINESS)
            val isAppointmentsEnabled = runCatching {
                isAppointmentsPluginEnabled(business.id)
            }.getOrElse { false }

            if (isAppointmentsEnabled) {
                add(DashboardFeature.APPOINTMENTS)
            }
        }
    }
}
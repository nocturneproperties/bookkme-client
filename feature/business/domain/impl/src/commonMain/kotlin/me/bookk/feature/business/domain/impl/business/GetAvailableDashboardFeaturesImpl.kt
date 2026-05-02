package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.entity.DashboardFeature

internal class GetAvailableDashboardFeaturesImpl : GetAvailableDashboardFeatures {
    override suspend fun invoke(): Set<DashboardFeature> {
        return setOf(
            DashboardFeature.BUSINESS,
            DashboardFeature.SHOP,
            DashboardFeature.APPOINTMENTS
        )
    }
}
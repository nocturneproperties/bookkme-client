package me.bookk.feature.business.domain.api.business

import me.bookk.feature.business.domain.api.entity.DashboardFeature

interface GetAvailableDashboardFeatures {
    suspend operator fun invoke(): Set<DashboardFeature>
}
package me.bookk.feature.business.domain.api

import me.bookk.feature.business.domain.api.entity.DashboardFeature

interface GetAvailableDashboardFeatures {
    suspend operator fun invoke(): Set<DashboardFeature>
}
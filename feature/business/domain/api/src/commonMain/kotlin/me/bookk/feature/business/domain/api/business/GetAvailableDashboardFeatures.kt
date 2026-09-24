package me.bookk.feature.business.domain.api.business

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.DashboardOverview

interface GetAvailableDashboardFeatures {
    operator fun invoke(): Flow<DashboardOverview?>
}

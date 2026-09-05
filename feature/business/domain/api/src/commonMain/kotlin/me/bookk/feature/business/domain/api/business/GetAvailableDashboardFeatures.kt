package me.bookk.feature.business.domain.api.business

import me.bookk.feature.business.domain.api.entity.DashboardFeature
import kotlin.uuid.Uuid

interface GetAvailableDashboardFeatures {
    suspend operator fun invoke(): Set<DashboardFeature>
    suspend fun cached(businessId: Uuid, onResultAvailable: suspend (Set<DashboardFeature>) -> Unit)
}
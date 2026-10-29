package me.bookk.feature.business.domain.api.business

import kotlin.uuid.Uuid

interface RefreshBusinessInfo {
    suspend operator fun invoke(applyDashboardIdFromRemote: Boolean = false)
    suspend fun refreshAndSelect(businessId: Uuid)
}

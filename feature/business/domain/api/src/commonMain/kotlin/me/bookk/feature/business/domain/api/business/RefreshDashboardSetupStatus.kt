package me.bookk.feature.business.domain.api.business

import kotlin.uuid.Uuid

interface RefreshDashboardSetupStatus {
    suspend operator fun invoke(businessId: Uuid)
}

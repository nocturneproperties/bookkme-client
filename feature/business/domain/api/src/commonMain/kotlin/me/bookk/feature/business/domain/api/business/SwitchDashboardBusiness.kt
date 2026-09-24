package me.bookk.feature.business.domain.api.business

import kotlin.uuid.Uuid

interface SwitchDashboardBusiness {
    suspend operator fun invoke(businessId: Uuid)
}

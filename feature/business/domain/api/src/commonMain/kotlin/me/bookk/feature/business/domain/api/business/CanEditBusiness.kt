package me.bookk.feature.business.domain.api.business

import kotlin.uuid.Uuid

interface CanEditBusiness {
    suspend operator fun invoke(businessId: Uuid): Boolean
}

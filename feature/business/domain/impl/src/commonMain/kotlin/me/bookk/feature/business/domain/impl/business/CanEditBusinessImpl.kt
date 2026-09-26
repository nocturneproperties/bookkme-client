package me.bookk.feature.business.domain.impl.business

import me.bookk.feature.business.domain.api.business.CanEditBusiness
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.uuid.Uuid

internal class CanEditBusinessImpl(
    private val businessDataSource: BusinessDataSource
) : CanEditBusiness {
    override suspend fun invoke(businessId: Uuid): Boolean {
        return businessDataSource.getBusinessById(businessId)?.permissions?.business?.update == true
    }
}

package me.bookk.feature.business.domain.impl

import me.bookk.feature.business.domain.api.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class RefreshBusinessInfoImpl(
    private val businessDataSource: BusinessDataSource
) : RefreshBusinessInfo {
    override suspend fun invoke(): Business? {
        val business = businessDataSource.getBusinessFromRemote()
        business?.let { businessDataSource.saveBusinessInDB(it) }
        return business
    }
}
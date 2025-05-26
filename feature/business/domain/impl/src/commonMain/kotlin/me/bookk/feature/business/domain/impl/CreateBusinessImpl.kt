package me.bookk.feature.business.domain.impl

import me.bookk.feature.business.domain.api.CreateBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class CreateBusinessImpl(
    private val businessDataSource: BusinessDataSource
) : CreateBusiness {
    override suspend fun invoke(name: String): Business {
        val business = businessDataSource.createBusiness(name)
        businessDataSource.saveBusinessInDB(business)
        return business
    }
}
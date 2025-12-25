package me.bookk.feature.business.domain.impl

import me.bookk.feature.business.domain.api.CreateBusiness
import me.bookk.feature.business.domain.api.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class CreateBusinessImpl(
    private val businessDataSource: BusinessDataSource,
    private val refreshBusinessInfo: RefreshBusinessInfo
) : CreateBusiness {
    override suspend fun invoke(name: String): Business {
        val business = businessDataSource.createBusiness(name, "UAH") //Temporary currency hardcode
        refreshBusinessInfo()
        return business
    }
}
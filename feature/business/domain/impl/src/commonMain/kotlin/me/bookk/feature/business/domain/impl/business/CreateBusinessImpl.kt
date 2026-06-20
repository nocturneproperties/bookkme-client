package me.bookk.feature.business.domain.impl.business

import kotlinx.datetime.TimeZone
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class CreateBusinessImpl(
    private val businessDataSource: BusinessDataSource,
    private val refreshBusinessInfo: RefreshBusinessInfo
) : CreateBusiness {
    override suspend fun invoke(name: String): Business {
        val business = businessDataSource.createBusiness(
            name = name,
            currencyCode = "UAH", //Temporary currency hardcode
            timeZone = TimeZone.currentSystemDefault()
        )
        refreshBusinessInfo()
        return business
    }
}
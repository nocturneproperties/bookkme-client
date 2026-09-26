package me.bookk.feature.business.domain.impl.business

import kotlinx.datetime.TimeZone
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class CreateBusinessImpl(
    private val businessDataSource: BusinessDataSource,
    private val refreshBusinessInfo: RefreshBusinessInfo,
    private val switchDashboardBusiness: SwitchDashboardBusiness
) : CreateBusiness {
    override suspend fun invoke(name: String): Business {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) throw CreateBusiness.Error.EmptyName()
        val business = businessDataSource.createBusiness(
            name = trimmedName,
            currencyCode = "UAH", //Temporary currency hardcode
            timeZone = TimeZone.currentSystemDefault()
        )
        refreshBusinessInfo()
        switchDashboardBusiness(business.id)
        return business
    }
}
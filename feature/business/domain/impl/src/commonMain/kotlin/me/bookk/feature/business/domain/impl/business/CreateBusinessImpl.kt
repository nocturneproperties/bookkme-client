package me.bookk.feature.business.domain.impl.business

import kotlinx.datetime.TimeZone
import library.device.api.DeviceFacade
import library.money.api.Money
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource

internal class CreateBusinessImpl(
    private val businessDataSource: BusinessDataSource,
    private val deviceFacade: DeviceFacade,
    private val refreshBusinessInfo: RefreshBusinessInfo,
    private val switchDashboardBusiness: SwitchDashboardBusiness
) : CreateBusiness {
    override suspend fun invoke(name: String): Business {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) throw CreateBusiness.Error.EmptyName()
        val business = businessDataSource.createBusiness(
            name = trimmedName,
            currencyCode = deviceCurrencyOrFallback().code,
            timeZone = TimeZone.currentSystemDefault()
        )
        refreshBusinessInfo()
        switchDashboardBusiness(business.id)
        return business
    }

    private fun deviceCurrencyOrFallback(): Money.SupportedCurrency {
        val deviceCurrencyCode = deviceFacade.getCurrencyCode()
        return Money.SupportedCurrency.entries.firstOrNull { it.code == deviceCurrencyCode } ?: FALLBACK_CURRENCY
    }

    private companion object {
        val FALLBACK_CURRENCY = Money.SupportedCurrency.USD
    }
}

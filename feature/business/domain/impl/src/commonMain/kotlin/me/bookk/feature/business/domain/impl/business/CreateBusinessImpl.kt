package me.bookk.feature.business.domain.impl.business

import kotlinx.datetime.TimeZone
import library.device.api.DeviceFacade
import library.money.api.Money
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes

internal class CreateBusinessImpl(
    private val businessDataSource: BusinessDataSource,
    private val deviceFacade: DeviceFacade,
    private val refreshBusinessInfo: RefreshBusinessInfo,
    private val switchDashboardBusiness: SwitchDashboardBusiness,
) : CreateBusiness {
    override suspend fun invoke(name: String): Business {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) throw CreateBusiness.Error.EmptyName()
        val business = runCatching {
            businessDataSource.createBusiness(
                name = trimmedName,
                currencyCode = deviceCurrencyOrFallback().code,
                timeZone = TimeZone.currentSystemDefault(),
            )
        }
            .onBusinessError {
                when (it.errorCode) {
                    BusinessErrorCodes.BUSINESS_ALREADY_EXIST -> {
                        throw CreateBusiness.Error.BusinessAlreadyExists()
                    }

                    BusinessErrorCodes.BUSINESS_NAME_VALIDATION_ERROR -> {
                        throw CreateBusiness.Error.InvalidName()
                    }

                    BusinessErrorCodes.BUSINESS_INVALID_CURRENCY -> {
                        throw CreateBusiness.Error.InvalidCurrency()
                    }
                }
            }
            .getOrThrow()
        refreshBusinessInfo.refreshAndSelect(business.id)
        switchDashboardBusiness(business.id)
        return business
    }

    private fun deviceCurrencyOrFallback(): Money.SupportedCurrency {
        val deviceCurrencyCode = deviceFacade.getCurrencyCode()
        return Money.SupportedCurrency.entries.firstOrNull { it.code == deviceCurrencyCode }
            ?: FALLBACK_CURRENCY
    }

    private companion object {
        val FALLBACK_CURRENCY = Money.SupportedCurrency.USD
    }
}

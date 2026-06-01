package me.bookk.feature.services.domain.impl

import library.money.api.Money
import me.bookk.feature.services.domain.api.GetBusinessCurrency
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import kotlin.uuid.Uuid

internal class GetBusinessCurrencyImpl(
    private val serviceDataSource: ServiceDataSource
) : GetBusinessCurrency {
    override suspend fun invoke(businessId: Uuid): Money.SupportedCurrency {
        return serviceDataSource.getBusinessCurrency(businessId)
    }
}
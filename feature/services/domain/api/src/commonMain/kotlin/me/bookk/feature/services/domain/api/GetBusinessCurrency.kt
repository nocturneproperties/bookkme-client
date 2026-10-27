package me.bookk.feature.services.domain.api

import library.money.api.Money
import kotlin.uuid.Uuid

interface GetBusinessCurrency {
    suspend operator fun invoke(businessId: Uuid): Money.SupportedCurrency
}
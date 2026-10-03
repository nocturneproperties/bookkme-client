package me.bookk.feature.appointments.domain.api.entity

import library.money.api.Money

data class PriceAdjustment(
    val additionalServices: List<ServiceSnapshot>,
    val price: Money,
    val reason: String?
)

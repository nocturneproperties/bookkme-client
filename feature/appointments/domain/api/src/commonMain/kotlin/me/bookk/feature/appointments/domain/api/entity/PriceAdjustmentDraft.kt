package me.bookk.feature.appointments.domain.api.entity

import library.money.api.Money
import kotlin.uuid.Uuid

data class PriceAdjustmentDraft(
    val additionalServiceIds: List<Uuid>,
    val price: Money,
    val reason: String?
)

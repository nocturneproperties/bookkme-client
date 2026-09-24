package me.bookk.feature.services.presentation

import library.money.api.Money
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

internal fun stubGroup(
    name: String = "Hair",
    businessId: Uuid = Uuid.random()
) = ServiceGroup(
    id = Uuid.random(),
    businessId = businessId,
    name = name,
    createdAt = Clock.System.now()
)

internal fun stubService(
    name: String = "Haircut",
    group: ServiceGroup = stubGroup()
) = Service(
    id = Uuid.random(),
    businessId = group.businessId,
    group = group,
    name = name,
    duration = 30.minutes,
    price = Money(10000L, Money.SupportedCurrency.UAH),
    isAvailable = true,
    createdAt = Clock.System.now()
)

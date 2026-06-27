package me.bookk.feature.services.domain.impl

import library.money.api.Money
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal fun stubServiceGroup(businessId: Uuid = Uuid.random()) = ServiceGroup(
    id = Uuid.random(),
    businessId = businessId,
    name = "Test Group",
    createdAt = Instant.fromEpochMilliseconds(0)
)

internal fun stubService(businessId: Uuid = Uuid.random()) = Service(
    id = Uuid.random(),
    businessId = businessId,
    group = stubServiceGroup(businessId),
    name = "Haircut",
    duration = Duration.parse("30m"),
    price = Money(1000L, Money.SupportedCurrency.USD),
    isAvailable = true,
    createdAt = Instant.fromEpochMilliseconds(0)
)

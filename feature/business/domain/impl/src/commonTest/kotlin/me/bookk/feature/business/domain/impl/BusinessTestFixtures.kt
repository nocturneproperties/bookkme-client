package me.bookk.feature.business.domain.impl

import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.feature.business.domain.api.entity.Business
import kotlin.uuid.Uuid

internal fun stubBusiness(id: Uuid = Uuid.random()) = Business(
    id = id,
    name = "Test Business",
    description = "",
    address = "",
    location = null,
    currency = Currency("USD"),
    timeZone = TimeZone.UTC,
    socials = emptyMap()
)

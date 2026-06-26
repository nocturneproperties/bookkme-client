package me.bookk.feature.business.domain.impl

import io.mockk.mockk
import kotlinx.datetime.TimeZone
import me.bookk.feature.business.domain.api.entity.Business
import kotlin.uuid.Uuid

internal fun stubBusiness(id: Uuid = Uuid.random()) = Business(
    id = id,
    name = "Test Business",
    description = "",
    address = "",
    location = null,
    currency = mockk(),
    timeZone = TimeZone.UTC,
    socials = emptyMap()
)

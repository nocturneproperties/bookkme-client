package me.bookk.feature.services.domain.api.service.entity

import library.money.api.Money
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import kotlin.time.Duration
import kotlin.uuid.Uuid

data class Service(
    val id: Uuid,
    val businessId: Uuid,
    val group: ServiceGroup,
    val name: String,
    val duration: Duration,
    val price: Money,
    val isAvailable: Boolean
)
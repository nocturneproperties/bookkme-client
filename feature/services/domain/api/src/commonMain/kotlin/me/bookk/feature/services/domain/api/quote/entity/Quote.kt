package me.bookk.feature.services.domain.api.quote.entity

import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

data class Quote(
    val id: Uuid,
    val services: List<Service>,
    val token: String
)

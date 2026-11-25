package me.bookk.feature.clients.domain.api.entity

import kotlin.uuid.Uuid

data class ClientsQuery(
    val businessId: Uuid,
    val search: String? = null,
)

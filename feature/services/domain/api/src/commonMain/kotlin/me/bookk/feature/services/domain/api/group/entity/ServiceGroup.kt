package me.bookk.feature.services.domain.api.group.entity

import kotlin.uuid.Uuid

data class ServiceGroup(
    val id: Uuid,
    val businessId: Uuid,
    val name: String
)
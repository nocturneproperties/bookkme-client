package me.bookk.feature.business.domain.api.entity

import kotlin.uuid.Uuid

sealed interface Client {
    val id: Uuid
    val name: String
    val lastName: String
    val phone: String
    val businessId: Uuid

    data class Detached(
        override val id: Uuid,
        override val name: String,
        override val lastName: String,
        override val phone: String,
        override val businessId: Uuid
    ) : Client

    data class Integrated(
        override val id: Uuid,
        override val name: String,
        override val lastName: String,
        override val phone: String,
        override val businessId: Uuid,
        val userId: Uuid
    ) : Client
}
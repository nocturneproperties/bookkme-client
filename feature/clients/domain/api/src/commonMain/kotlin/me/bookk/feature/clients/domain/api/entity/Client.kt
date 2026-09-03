package me.bookk.feature.clients.domain.api.entity

import kotlin.uuid.Uuid

sealed interface Client {
    val id: Uuid
    val name: String
    val lastName: String
    val phone: String?
    val email: String?
    val businessId: Uuid
    val description: String?
    val fullName: String

    data class Detached(
        override val id: Uuid,
        override val name: String,
        override val lastName: String,
        override val phone: String?,
        override val email: String?,
        override val businessId: Uuid,
        override val description: String? = null
    ) : Client {
        override val fullName: String = "$name $lastName"
    }

    data class Integrated(
        override val id: Uuid,
        override val name: String,
        override val lastName: String,
        override val phone: String?,
        override val email: String?,
        override val businessId: Uuid,
        val userId: Uuid,
        override val description: String? = null
    ) : Client {
        override val fullName: String = "$name $lastName"
    }
}
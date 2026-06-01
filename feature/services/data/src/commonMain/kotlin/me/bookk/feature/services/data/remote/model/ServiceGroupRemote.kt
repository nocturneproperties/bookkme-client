package me.bookk.feature.services.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
internal class ServiceGroupRemote(
    val id: Uuid,
    val businessId: Uuid,
    val name: String,
    val createdAt: Instant
) {
    fun toDomain(): ServiceGroup {
        return ServiceGroup(
            id = id,
            businessId = businessId,
            name = name,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(group: ServiceGroup): ServiceGroupRemote {
            return ServiceGroupRemote(
                id = group.id,
                businessId = group.businessId,
                name = group.name,
                createdAt = group.createdAt
            )
        }
    }
}
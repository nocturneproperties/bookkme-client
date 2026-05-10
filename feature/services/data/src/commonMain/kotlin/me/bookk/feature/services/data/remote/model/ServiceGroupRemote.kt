package me.bookk.feature.services.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import kotlin.uuid.Uuid

@Serializable
internal class ServiceGroupRemote(
    val id: Uuid,
    val businessId: Uuid,
    val name: String
) {
    fun toDomain(): ServiceGroup {
        return ServiceGroup(
            id = id,
            businessId = businessId,
            name = name
        )
    }

    companion object {
        fun fromDomain(group: ServiceGroup): ServiceGroupRemote {
            return ServiceGroupRemote(
                id = group.id,
                businessId = group.businessId,
                name = group.name
            )
        }
    }
}
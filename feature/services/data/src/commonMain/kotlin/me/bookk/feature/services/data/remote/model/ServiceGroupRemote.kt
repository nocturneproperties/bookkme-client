package me.bookk.feature.services.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
internal class ServiceGroupRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3) val name: String,
    @ProtoNumber(4) val createdAt: Instant
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

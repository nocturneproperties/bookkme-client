package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.business.domain.api.entity.BusinessLink
import kotlin.uuid.Uuid

@Serializable
internal class BusinessLinkRemote(
    @ProtoNumber(1) val businessId: Uuid,
    @ProtoNumber(2) val name: String,
) {
    fun toDomain(): BusinessLink {
        return BusinessLink(businessId = businessId, name = name)
    }
}

@Serializable
internal class BusinessLinkRequest(
    @ProtoNumber(1) val name: String,
)

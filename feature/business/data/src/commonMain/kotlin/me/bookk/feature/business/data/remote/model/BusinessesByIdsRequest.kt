package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.uuid.Uuid

@Serializable
internal class BusinessesByIdsRequest(
    @ProtoNumber(1) val ids: List<Uuid> = emptyList(),
)

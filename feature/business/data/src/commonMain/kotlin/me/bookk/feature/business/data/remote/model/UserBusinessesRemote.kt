package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.uuid.Uuid

@Serializable
class UserBusinessesRemote(
    @ProtoNumber(1) val dashboardId: Uuid?,
    @ProtoNumber(2) val businesses: List<BusinessRemote>
)

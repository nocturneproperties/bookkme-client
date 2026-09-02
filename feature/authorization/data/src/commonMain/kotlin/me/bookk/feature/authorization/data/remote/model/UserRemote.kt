package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.uuid.Uuid

@Serializable
internal class UserProfileRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val firstName: String,
    @ProtoNumber(3) val lastName: String,
    @ProtoNumber(4) val email: String,
    @ProtoNumber(5) val phone: String?
)

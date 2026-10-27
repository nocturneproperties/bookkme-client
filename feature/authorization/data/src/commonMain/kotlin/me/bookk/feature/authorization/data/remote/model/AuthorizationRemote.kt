package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class TokenInfoResponse(
    @ProtoNumber(1) val accessToken: String,
    @ProtoNumber(2) val refreshToken: String
)

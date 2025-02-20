package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class TokenInfoResponse(
    val accessToken: String,
    val refreshToken: String
)
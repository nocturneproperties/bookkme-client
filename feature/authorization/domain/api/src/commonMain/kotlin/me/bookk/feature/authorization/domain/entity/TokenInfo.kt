package me.bookk.feature.authorization.domain.entity

data class TokenInfo(
    val accessToken: String,
    val refreshToken: String
)
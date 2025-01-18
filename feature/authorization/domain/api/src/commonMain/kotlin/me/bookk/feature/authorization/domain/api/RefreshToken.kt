package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.TokenInfo

interface RefreshToken {
    suspend fun invoke(refreshToken: String): TokenInfo
}
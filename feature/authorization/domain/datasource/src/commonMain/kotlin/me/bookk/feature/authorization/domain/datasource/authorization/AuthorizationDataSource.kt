package me.bookk.feature.authorization.domain.datasource.authorization

import me.bookk.feature.authorization.domain.entity.TokenInfo

interface AuthorizationDataSource {
    suspend fun saveAuthorizationTokens(tokenInfo: TokenInfo)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun refreshToken(refreshToken: String): TokenInfo
}
package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.RefreshToken
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.entity.TokenInfo

internal class RefreshTokenImpl(
    private val authorizationDataSource: AuthorizationDataSource
) : RefreshToken {
    override suspend fun invoke(refreshToken: String): TokenInfo {
        val info = authorizationDataSource.refreshToken(refreshToken)
        authorizationDataSource.saveAuthorizationTokens(info)
        return info
    }
}
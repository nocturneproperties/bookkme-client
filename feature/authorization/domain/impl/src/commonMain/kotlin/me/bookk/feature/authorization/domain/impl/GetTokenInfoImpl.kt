package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.entity.TokenInfo

internal class GetTokenInfoImpl(
    private val authorizationDataSource: AuthorizationDataSource
) : GetTokenInfo {
    override suspend fun invoke(): TokenInfo? {
        val access = authorizationDataSource.getAccessToken()
        val refresh = authorizationDataSource.getRefreshToken()
        if (access != null && refresh != null) {
            return TokenInfo(access, refresh)
        }
        return null
    }
}
package me.bookk.feature.authorization.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource

internal class IsUserLoggedInImpl(
    private val authorizationDataSource: AuthorizationDataSource
) : IsUserLoggedIn {
    override suspend fun invoke(): Boolean {
        return authorizationDataSource.getAccessToken() != null
    }

    override fun asFlow(): Flow<Boolean> {
        return authorizationDataSource.getIsAuthorizedFlow()
    }
}
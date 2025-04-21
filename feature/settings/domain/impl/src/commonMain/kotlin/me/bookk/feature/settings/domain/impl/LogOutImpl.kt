package me.bookk.feature.settings.domain.impl

import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.settings.domain.api.LogOut

internal class LogOutImpl(
    private val authorizationDataSource: AuthorizationDataSource
) : LogOut {
    override suspend fun invoke() {
        runCatching { authorizationDataSource.logOut() }
        authorizationDataSource.saveAuthorizationTokens(null)
        authorizationDataSource.setAuthorizationStatus(false)
    }
}
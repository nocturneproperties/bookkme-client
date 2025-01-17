package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.core.storage.PreferenceProvider
import me.bookk.core.storage.Preferences
import me.bookk.core.storage.get
import me.bookk.core.storage.set
import me.bookk.feature.authorization.data.mapping.toDomain
import me.bookk.feature.authorization.data.remote.api.AuthRouting
import me.bookk.feature.authorization.data.remote.model.RefreshTokenRemote
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.entity.TokenInfo

class CommonAuthorizationDataSource(
    private val httpClient: HttpClient,
    preferenceProvider: PreferenceProvider
) : AuthorizationDataSource, DataSource() {

    private val preferences = preferenceProvider.get("authorization_prefs")

    override suspend fun saveAuthorizationTokens(tokenInfo: TokenInfo) {
        preferences.set(Key.accessToken, tokenInfo.accessToken)
        preferences.set(Key.refreshToken, tokenInfo.refreshToken)
    }

    override suspend fun getAccessToken(): String? {
        return preferences.get(Key.accessToken)
    }

    override suspend fun getRefreshToken(): String? {
        return preferences.get(Key.refreshToken)
    }

    override suspend fun refreshToken(refreshToken: String): TokenInfo = mapExceptions {
        val response = httpClient.post(AuthRouting.Api.Auth.Refresh()) {
            setBody(RefreshTokenRemote(refreshToken))
        }
        response.body<TokenInfoResponse>().toDomain()
    }

    private object Key {
        val accessToken = Preferences.Key<String>("access_token")
        val refreshToken = Preferences.Key<String>("refresh_token")
    }
}
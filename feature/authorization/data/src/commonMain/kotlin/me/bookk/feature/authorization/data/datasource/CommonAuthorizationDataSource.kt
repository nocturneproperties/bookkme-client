package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.setBody
import io.ktor.util.PlatformUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.getFlow
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.feature.authorization.data.BuildKonfig
import me.bookk.feature.authorization.data.mapping.toDomain
import me.bookk.feature.authorization.data.mapping.toRemote
import me.bookk.feature.authorization.data.remote.api.AuthRouting.Api.Auth
import me.bookk.feature.authorization.data.remote.model.AuthChallengeResponse
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.authorization.domain.datasource.authorization.ServerAuthenticationChallenge
import me.bookk.feature.authorization.domain.datasource.authorization.SignInData
import me.bookk.feature.authorization.domain.entity.TokenInfo

class CommonAuthorizationDataSource(
    private val httpClient: HttpClient,
    private val noAuthHttpClient: HttpClient,
    preferenceProvider: PreferenceProvider
) : AuthorizationDataSource, DataSource() {

    private val preferences = preferenceProvider.get("authorization_prefs")

    init {
        if (!PlatformUtils.IS_JVM) {
            runBlocking {
                if (getRefreshToken() == null) {
                    val refresh =
                        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJib29ra2subWUiLCJpc3MiOiJjb20uYm9va2suc2VydmVyLnJlZnJlc2giLCJqdGkiOiI2NDNkMDljYjM0MDA0YjZlOTgxOWU0MzUwNWVjOTk1ZSIsImRldmljZV9pZCI6MTYsImlhdCI6MTc0NzY5MDAyNiwibmJmIjoxNzQ3NjkwMDI2LCJleHAiOjE3NDgyOTQ4MjZ9.CI6801U7JIZrQqz28yjCEuXjEM4AmrxffSYFziz3EqLhRmxSCtNXZ_76Lhif_1VUzvzs0jziPZFRSW6Ii36VBYKNQ35mO7_rKjJKksTZoWKM3u7otE3WoXo_dbs0OcyEfP5F-auRWASaHBHuSbRLkekhfbHnD9jOs7rd4EDg9143nFEfa0cJxzSO-zzW09h2WqULOA8i9vUE0nlULmBR8nxahtiaUVxmcHUR0bXouH3NDGV6y6m7zHCmQy5c7a0_CbyqU2GhxHnlDSC-GDngwXyef8vqX3vP0bBeJBuAKkS_vwHUTCNTZrjGPPYQW1p1uVTLS_w18QG4pfwycSnPjg"
                    val access =
                        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJib29ra2subWUiLCJpc3MiOiJjb20uYm9va2suc2VydmVyIiwianRpIjoiZWNiNzBjZjQyNzg3NDQyM2FjMjQ1MjAxMDFmN2QzNzQiLCJhdXRoX2lkIjo0LCJ1c2VyX2lkIjo0LCJkZXZpY2VfaWQiOjE2LCJpYXQiOjE3NDc2OTAwMjYsIm5iZiI6MTc0NzY5MDAyNiwiZXhwIjoxNzQ3NjkwMzI2fQ.JKmiXNRYQA7QCW-EjvsCpvAWW8XxhWW8HdzotluGrAYByp5A4jQsIYv-dTjyUYPl3ag2jnE0igId3oU7_c1iUZ9FbCdELrKZe-wtQB7oKVRPgyryRLbMtjrasQdCJzQCm1lOEeELiMGeUbWh4ADwykfYM24pIfl_59IhRvSBDiHx6lbo4yFO7mJBRC5M3w3CbA5LXb7CeVO3yUn2BVizTmyaQgJthxjs-I7etixOD4_pUWdivD_tSwjIyq3htiO-jFETtwwC8eXUumRf29sfALpnQsS98nt9o8YxE5QmzcLSKU9oUsWBVy6Q629DmZSBVAZb7yzYOG_cjTPbCllgwQ"
                    saveAuthorizationTokens(TokenInfo(access, refresh))
                    setAuthorizationStatus(true)
                    invalidateClientTokens()
                }
            }
        }
    }

    override suspend fun saveAuthorizationTokens(tokenInfo: TokenInfo?) {
        preferences.set(Key.accessToken, tokenInfo?.accessToken)
        preferences.set(Key.refreshToken, tokenInfo?.refreshToken)
    }

    override suspend fun getAccessToken(): String? {
        return preferences.get(Key.accessToken)
    }

    override fun getIsAuthorizedFlow(): Flow<Boolean> {
        return preferences.getFlow(Key.authorized).map {
            if (BuildKonfig.VARIANT.contains("mock")) {
                true
            } else {
                it ?: false
            }
        }
    }

    override suspend fun getRefreshToken(): String? {
        return preferences.get(Key.refreshToken)
    }

    override suspend fun refreshToken(refreshToken: String): TokenInfo = mapExceptions {
        val response = noAuthHttpClient.post(Auth.Refresh()) {
            bearerAuth(refreshToken)
        }
        response.body<TokenInfoResponse>().toDomain()
    }

    override suspend fun getAuthorizationChallenge(): ServerAuthenticationChallenge =
        mapExceptions {
            val response = httpClient.get(Auth.PassKey.SignInChallenge())
            response.body<AuthChallengeResponse>().toDomain()
        }

    override suspend fun verifyAuthorization(signInData: SignInData): TokenInfo {
        return mapExceptions {
            val response = httpClient.post(Auth.SignIn()) {
                setBody(signInData.toRemote())
            }
            response.body<TokenInfoResponse>().toDomain()
        }
    }

    override suspend fun deleteAccount(request: DeleteAccountRequest) {
        mapExceptions {
            httpClient.delete(Auth.Account()) {
                setBody(request.toRemote())
            }
        }
    }

    override suspend fun logOut() {
        return mapExceptions { httpClient.delete(Auth.SignOut()) }
    }

    override suspend fun setAuthorizationStatus(isAuthorized: Boolean) {
        preferences.set(Key.authorized, isAuthorized)
    }

    override suspend fun invalidateClientTokens() {
        httpClient.authProvider<BearerAuthProvider>()?.clearToken()
    }

    private object Key {
        val accessToken = Preferences.Key<String>("access_token")
        val refreshToken = Preferences.Key<String>("refresh_token")
        val authorized = Preferences.Key<Boolean>("authorized")
    }
}
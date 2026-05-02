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
import me.bookk.core.domain.logout.LogOutAction
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
) : AuthorizationDataSource, DataSource(), LogOutAction {

    private val preferences = preferenceProvider.get("authorization_prefs")

    init {
        if (!PlatformUtils.IS_JVM) {
            runBlocking {
                if (getRefreshToken() == null) {
                    val refresh =
                        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJib29ra21lLmFwcCIsImlzcyI6ImNvbS5ib29ray5zZXJ2ZXIucmVmcmVzaCIsImp0aSI6ImExNmVkNjNiLTgzYjktNGMwOC04NDFhLWExNDc5YzM4MzJlZSIsImRldmljZV9pZCI6IjFmOGM4ZDA1LTU4YzctNDdjYi1iM2ZiLTUwNWJkNGFiZDAwYiIsImlhdCI6MTc3NzY1OTIwMSwibmJmIjoxNzc3NjU5MjAxLCJleHAiOjE3NzgyNjQwMDF9.a_Mf3czhZukMQTFXv_C6dT1qVIuYichC5t4lYIi2kpZorX2vRw6DlY8lidFyN1n8cB-uIMuaaWVh2cUU2PBYGS52dsvdLf_taCZeSNxP13-w1-Q7IY03ljofeQGJHE6j-F8GKQf_0jYsQc4QXz1ml_OJumPS_Xo_T-5myz1QUO8WFbJ_RrPnkb1DYYQ10w9FQAj_MzHK7h9ukLodfQLUKVsCqMAbaOoyEtIz8DsTa9oOLBTrA6JrO_VfAshyi5jQDyHOLwC7wR7Ynq2k-Z8XW3_B3oHMizptmx5ZH4qpeNOU7IsdVMrn8D34iz5MZyCQ17IK2VZKGov3x-pvunl3Iw"
                    val access =
                        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJib29ra21lLmFwcCIsImlzcyI6ImNvbS5ib29ray5zZXJ2ZXIiLCJqdGkiOiIwNDI5MGIzYS1hODUwLTQwMzgtYjcyNy01MDU1YjdmYzNlMmYiLCJhdXRoX2lkIjoiOTk2NWQ4ZjYtOWM2ZS00ZTY4LTg3YjMtMWRjNDc3ODJkZjg2IiwidXNlcl9pZCI6IjA5NDU0ZjZkLTY3ZTUtNDhiOS1iNzhjLTdmMWZkYmMwNjM4MCIsImRldmljZV9pZCI6IjFmOGM4ZDA1LTU4YzctNDdjYi1iM2ZiLTUwNWJkNGFiZDAwYiIsImlhdCI6MTc3NzY1OTIwMSwibmJmIjoxNzc3NjU5MjAxLCJleHAiOjE3Nzc2NTk1MDF9.AbOuaEx9ybyTnp5hpJ4fT9tEDQ-dLQE8DHKMPE4fi4nJxrWgsfbqhCrpGsFKHZgP8bRcdat7HYpMabngdDvjxSS1ZHOlPzb3_F5vbSQElFfMMt3IFQpFJrveYb7Sv4-spsuf9xNe5lGaZU-IuJbGDmyt-47Tax_5GGM_9FybRAGO3PonxrZXhLM25Gbdk5CkSBXr5DsRVBq8KCEMQY2SaTXWuz12NZzqjSa4eUwg6bdc2HrwsuxcZ9uuNGZu3mEdgge5crEB6O7DhIWnqDaOwUOJP0L_yIGLljBmkmkPPqTzoTpbdXbaf_PlN_zKupUhf8gtyEnXnAXIXPdSVKU3hA"
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

    override suspend fun doOnLogOut() {
        saveAuthorizationTokens(null)
        setAuthorizationStatus(false)
        mapExceptions { httpClient.delete(Auth.SignOut()) }
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
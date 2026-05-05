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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        preferences.clear()
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
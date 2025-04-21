package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.setBody
import io.ktor.util.PlatformUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import me.bookk.core.data.DataSource
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
import me.bookk.feature.platform.domain.datasource.PreferenceProvider
import me.bookk.feature.platform.domain.datasource.Preferences
import me.bookk.feature.platform.domain.datasource.get
import me.bookk.feature.platform.domain.datasource.getFlow
import me.bookk.feature.platform.domain.datasource.set

class CommonAuthorizationDataSource(
    private val httpClient: HttpClient,
    private val noAuthHttpClient: HttpClient,
    preferenceProvider: PreferenceProvider
) : AuthorizationDataSource, DataSource() {

    private val preferences = preferenceProvider.get("authorization_prefs")

    init {
        if (!PlatformUtils.IS_JVM) {
            val access =
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJib29ra2subWUiLCJpc3MiOiJjb20uYm9va2suc2VydmVyIiwianRpIjoiZGI1YTFmYjAtNWQwNy00OTdiLWI2ZjMtMjhiNWZkOWRlZTdhIiwiYXV0aF9pZCI6MywidXNlcl9pZCI6MywiZGV2aWNlX2lkIjo4LCJpYXQiOjE3NDQzMDgxMjgsIm5iZiI6MTc0NDMwODEyOCwiZXhwIjoxNzQ0MzExNzI4fQ.dHq5FBi6n1Y87Vfd8HyEmGUMGDkhpNYcINSYh9i-nlBzb-_j2FoQIDWVppv-_9wQFodoaq98y-vb-QPU3O8RvK5WlIpCLX4BD-DYL8d2eb1Ms3Ls7yzli1x73tNlnWGL4_v_MbjW9BkHrHOvrJyEJ8T-tHuquejUtC5trnEehzEwnRaRHtRDTDKUAd2dYajGajVBrnxapvxPkQXJkUdwFJ67WDj1Kkt23RJDx2tPN-FrJ27_naWlN90Fi5wPZ6mOVfk0E-jHrR5HlDw7RfLtw-eSdGPThFitjwdfi71kSTOcezfflZiTqH5db1-M1GTK5XVTq-i89jalKbfluOjxEA"
            val refresh =
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJib29ra2subWUiLCJpc3MiOiJjb20uYm9va2suc2VydmVyLnJlZnJlc2giLCJqdGkiOiIyMmE3ZTA3Ny02ZjFkLTRhMTMtYWJjMi1kYTJjMzU2M2E3ODQiLCJkZXZpY2VfaWQiOjgsImlhdCI6MTc0NDMwODEyOCwibmJmIjoxNzQ0MzA4MTI4LCJleHAiOjE3NDQzOTQ1Mjh9.eRoxakH2TDnooUncF5QY9msg26Mi8HKxnSd4nmGLWexONQJazLosjqUUDfNgD6U9w8WpfkVy-Q1wojgNOlMAPQ-msYbOjcdmk0BS3SLdA53Vtr33p9WNXaCITtvJyBkaSyDHGge-NUb-zm95gAouw-48GpVYxiBjhPbYhy1tEGtEWHc6LoxAzlHD7p1cSJVhL7M5SPSo3Ac-i1hMD_Wp5CWDtpzQlzxjEN2xexJUeFTqv1TxhEl1ReUjDMGGxTepfdu8PE5uYK8osgAH3th7bO3K10m_ZTJQqd5FF0BYiJi5hNkj9SGrFNpPdRwhSGdFaUNFtu400ReGyJix-GOkrw"
            runBlocking {
                if (getRefreshToken() == null) {
                    saveAuthorizationTokens(TokenInfo(access, refresh))
                    setAuthorizationStatus(true)
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
        return preferences.getFlow(Key.authorized).map { it ?: false }
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
            val response = httpClient.get(Auth.PassKey.SignInChallenge()) {}
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

    private object Key {
        val accessToken = Preferences.Key<String>("access_token")
        val refreshToken = Preferences.Key<String>("refresh_token")
        val authorized = Preferences.Key<Boolean>("authorized")
    }
}
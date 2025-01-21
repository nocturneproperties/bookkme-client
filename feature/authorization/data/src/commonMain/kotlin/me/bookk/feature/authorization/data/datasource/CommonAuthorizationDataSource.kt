package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.core.domain.entity.Error
import me.bookk.feature.authorization.data.local.PassKeyManager
import me.bookk.feature.authorization.data.mapping.toDomain
import me.bookk.feature.authorization.data.mapping.toRemote
import me.bookk.feature.authorization.data.remote.api.AuthRouting.Api.Auth
import me.bookk.feature.authorization.data.remote.error.AuthErrorCodes
import me.bookk.feature.authorization.data.remote.model.RefreshTokenRemote
import me.bookk.feature.authorization.data.remote.model.SignInStartResponse
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.ServerSignInChallenge
import me.bookk.feature.authorization.domain.datasource.authorization.SignInData
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.entity.TokenInfo
import me.bookk.feature.platform.domain.datasource.PreferenceProvider
import me.bookk.feature.platform.domain.datasource.Preferences
import me.bookk.feature.platform.domain.datasource.get
import me.bookk.feature.platform.domain.datasource.set

class CommonAuthorizationDataSource(
    private val httpClient: HttpClient,
    private val passKeyManager: PassKeyManager,
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
        val response = httpClient.post(Auth.Refresh()) {
            setBody(RefreshTokenRemote(refreshToken))
        }
        response.body<TokenInfoResponse>().toDomain()
    }

    override suspend fun getAuthorizationChallenge(): ServerSignInChallenge = mapExceptions {
        val response = httpClient.get(Auth.SignIn.PassKey.Challenge()) {}
        response.body<SignInStartResponse>().toDomain()
    }

    override suspend fun verifyAuthorization(signInData: SignInData): TokenInfo {
        return mapExceptions(
            action =  {
                val response = httpClient.post(Auth.SignIn.PassKey.Validate()) {
                    setBody(signInData.toRemote())
                }
                response.body<TokenInfoResponse>().toDomain()
            },
            businessExceptionMapper = {
                when (it.errorCode) {
                    AuthErrorCodes.PASSKEY_OWNER_NOT_FOUND -> SignIn.Error.NoAccountForThisPasskey
                    AuthErrorCodes.VERIFICATION_FAILED,
                    AuthErrorCodes.CHALLENGE_WINDOW_EXPIRED -> SignIn.Error.PasskeyVerificationFailed
                    else -> it
                }
            }
        )
    }

    override suspend fun requestPasskey(challenge: ServerSignInChallenge): PasskeyVerificationPayload {
        return mapExceptions(
            action = { passKeyManager.authorize(challenge.challengeJson) },
            exceptionMapper = {
                if (it !is Error.WrappedError) return@mapExceptions it
                val cause = it.cause
                if (cause !is PassKeyManager.Error) return@mapExceptions it
                when (cause) {
                    PassKeyManager.Error.CredentialsMissing -> SignIn.Error.NoCredentialsAvailable
                    PassKeyManager.Error.Infrastructure,
                    PassKeyManager.Error.Unknown -> SignIn.Error.PasskeyVerificationFailed
                    PassKeyManager.Error.UserCancelled -> Error.Ignore(cause)
                }
            }
        )
    }

    private object Key {
        val accessToken = Preferences.Key<String>("access_token")
        val refreshToken = Preferences.Key<String>("refresh_token")
    }
}
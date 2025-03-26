package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.util.AttributeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.data.DataSource
import me.bookk.core.domain.entity.Error
import me.bookk.feature.authorization.data.local.PassKeyManager
import me.bookk.feature.authorization.data.mapping.toDomain
import me.bookk.feature.authorization.data.mapping.toRemote
import me.bookk.feature.authorization.data.remote.api.AuthRouting.Api.Auth
import me.bookk.feature.authorization.data.remote.error.AuthErrorCodes
import me.bookk.feature.authorization.data.remote.model.AuthChallengeResponse
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.domain.api.PasskeyVerification
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.authorization.domain.datasource.authorization.ServerAuthenticationChallenge
import me.bookk.feature.authorization.domain.datasource.authorization.SignInData
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.entity.TokenInfo
import me.bookk.feature.platform.domain.datasource.PreferenceProvider
import me.bookk.feature.platform.domain.datasource.Preferences
import me.bookk.feature.platform.domain.datasource.get
import me.bookk.feature.platform.domain.datasource.getFlow
import me.bookk.feature.platform.domain.datasource.set

class CommonAuthorizationDataSource(
    private val httpClient: HttpClient,
    private val passKeyManager: PassKeyManager,
    preferenceProvider: PreferenceProvider
) : AuthorizationDataSource, DataSource() {

    private val preferences = preferenceProvider.get("authorization_prefs")

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
        val response = httpClient.post(Auth.Refresh()) {
            // Replacement for markAsRefreshTokenRequest(), because its available only inside Auth plugin closure
            attributes.put(AttributeKey("auth-request"), Unit)
            header(HttpHeaders.Authorization, "Bearer $refreshToken")
        }
        response.body<TokenInfoResponse>().toDomain()
    }

    override suspend fun getAuthorizationChallenge(): ServerAuthenticationChallenge =
        mapExceptions {
            val response = httpClient.get(Auth.PassKey.SignInChallenge()) {}
            response.body<AuthChallengeResponse>().toDomain()
        }

    override suspend fun verifyAuthorization(signInData: SignInData): TokenInfo {
        return mapExceptions(
            action = {
                val response = httpClient.post(Auth.SignIn()) {
                    setBody(signInData.toRemote())
                }
                response.body<TokenInfoResponse>().toDomain()
            },
            businessExceptionMapper = {
                when (it.errorCode) {
                    AuthErrorCodes.PASSKEY_OWNER_NOT_FOUND -> PasskeyVerification.Error.NoAccountForThisPasskey
                    AuthErrorCodes.VERIFICATION_FAILED,
                    AuthErrorCodes.CHALLENGE_WINDOW_EXPIRED -> PasskeyVerification.Error.PasskeyVerificationFailed

                    else -> it
                }
            }
        )
    }

    override suspend fun requestPasskey(challenge: ServerAuthenticationChallenge): PasskeyVerificationPayload {
        return mapExceptions(
            action = { passKeyManager.authorize(challenge.challengeJson) },
            exceptionMapper = {
                if (it !is Error.WrappedError) return@mapExceptions it
                val cause = it.cause
                if (cause !is PassKeyManager.Error) return@mapExceptions it
                when (cause) {
                    PassKeyManager.Error.CredentialsMissing -> PasskeyVerification.Error.NoCredentialsAvailable
                    PassKeyManager.Error.Infrastructure,
                    PassKeyManager.Error.Unknown -> PasskeyVerification.Error.PasskeyVerificationFailed

                    PassKeyManager.Error.UserCancelled -> Error.Ignore(cause)
                }
            }
        )
    }

    override suspend fun deleteAccount(request: DeleteAccountRequest) {
        return mapExceptions(
            action = {
                httpClient.delete(Auth.Account()) {
                    setBody(request.toRemote())
                }
            },
            exceptionMapper = {
                if (it !is Error.WrappedError) return@mapExceptions it
                val cause = it.cause
                if (cause !is PassKeyManager.Error) return@mapExceptions it
                when (cause) {
                    PassKeyManager.Error.CredentialsMissing -> PasskeyVerification.Error.NoCredentialsAvailable
                    PassKeyManager.Error.Infrastructure,
                    PassKeyManager.Error.Unknown -> PasskeyVerification.Error.PasskeyVerificationFailed

                    PassKeyManager.Error.UserCancelled -> Error.Ignore(cause)
                }
            }
        )
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
package me.bookk.feature.authorization.domain.datasource.authorization

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.entity.TokenInfo

interface AuthorizationDataSource {
    suspend fun saveAuthorizationTokens(tokenInfo: TokenInfo?)
    suspend fun getAccessToken(): String?
    fun getAccessTokenFlow(): Flow<String?>
    suspend fun getRefreshToken(): String?
    suspend fun refreshToken(refreshToken: String): TokenInfo
    suspend fun getAuthorizationChallenge(): ServerAuthenticationChallenge
    suspend fun verifyAuthorization(signInData: SignInData): TokenInfo
    suspend fun requestPasskey(challenge: ServerAuthenticationChallenge): PasskeyVerificationPayload
    suspend fun deleteAccount(request: DeleteAccountRequest)
    suspend fun logOut()
}
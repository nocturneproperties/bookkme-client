package me.bookk.feature.authorization.domain.datasource.authorization

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.authorization.domain.entity.TokenInfo

interface AuthorizationDataSource {
    suspend fun saveAuthorizationTokens(tokenInfo: TokenInfo?)
    suspend fun getAccessToken(): String?
    fun getIsAuthorizedFlow(): Flow<Boolean>
    suspend fun getRefreshToken(): String?
    suspend fun refreshToken(refreshToken: String): TokenInfo
    suspend fun getAuthorizationChallenge(): ServerAuthenticationChallenge
    suspend fun verifyAuthorization(signInData: SignInData): TokenInfo
    suspend fun deleteAccount(request: DeleteAccountRequest)
    suspend fun setAuthorizationStatus(isAuthorized: Boolean)
    suspend fun invalidateClientTokens()
}
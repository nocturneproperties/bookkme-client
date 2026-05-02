package me.bookk.feature.authorization.domain.impl

import library.device.api.DeviceFacade
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.entity.businessOrThrow
import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.ServerAuthenticationChallenge
import me.bookk.feature.authorization.domain.datasource.authorization.SignInData
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.entity.TokenInfo
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo

internal class SignInImpl(
    private val deviceDataSource: DeviceDataSource,
    private val authorizationDataSource: AuthorizationDataSource,
    private val deviceFacade: DeviceFacade,
    private val userProfileCRUD: UserProfileCRUD,
    private val passKeyManager: PassKeyManager,
    private val refreshBusiness: RefreshBusinessInfo
) : SignIn {
    override suspend fun invoke() {
        val challenge = authorizationDataSource.getAuthorizationChallenge()
        val payload = authorizeWithPasskey(challenge)
        val tokenInfo = verifyAuthorization(challenge, payload)
        authorizationDataSource.saveAuthorizationTokens(tokenInfo)
        authorizationDataSource.invalidateClientTokens()
        userProfileCRUD.updateFromRemote()
        runCatching { refreshBusiness() }
        authorizationDataSource.setAuthorizationStatus(true)
    }

    private suspend fun SignInImpl.verifyAuthorization(
        challenge: ServerAuthenticationChallenge,
        payload: PasskeyVerificationPayload
    ): TokenInfo {
        return runCatching {
            authorizationDataSource.verifyAuthorization(
                createSignInData(challenge.requestId, payload.jsonPayload)
            )
        }.recoverCatching {
            throw when (it.businessOrThrow().errorCode) {
                AuthErrorCodes.PASSKEY_OWNER_NOT_FOUND -> SignIn.Error.NoAccountForThisPasskey
                AuthErrorCodes.VERIFICATION_FAILED,
                AuthErrorCodes.CHALLENGE_WINDOW_EXPIRED -> SignIn.Error.PasskeyVerificationFailed

                else -> it
            }
        }.getOrThrow()
    }

    private suspend fun authorizeWithPasskey(challenge: ServerAuthenticationChallenge): PasskeyVerificationPayload {
        return runCatching { passKeyManager.authorize(challenge.challengeJson) }
            .recoverCatching {
                throw when (it) {
                    PassKeyManager.Error.UserCancelled -> Error.Ignore(it)
                    PassKeyManager.Error.CredentialsMissing -> SignIn.Error.NoCredentialsAvailable
                    else -> SignIn.Error.PasskeyVerificationFailed
                }
            }.getOrThrow()
    }

    private suspend fun createSignInData(requestId: String, publicCredJson: String): SignInData {
        return SignInData(
            requestId = requestId,
            deviceInfo = SignInData.DeviceInfo(
                deviceUUID = deviceDataSource.getOrCreateDeviceUUID(),
                deviceName = deviceFacade.getDeviceName()
            ),
            publicKeyCredentialJson = publicCredJson
        )
    }
}
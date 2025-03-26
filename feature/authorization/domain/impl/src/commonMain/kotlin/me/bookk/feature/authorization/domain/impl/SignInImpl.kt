package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.SignInData
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.platform.domain.api.GetPlatformInformation

internal class SignInImpl(
    private val deviceDataSource: DeviceDataSource,
    private val authorizationDataSource: AuthorizationDataSource,
    private val getPlatformInformation: GetPlatformInformation,
    private val userProfileCRUD: UserProfileCRUD,
) : SignIn {
    override suspend fun invoke() {
        val challenge = authorizationDataSource.getAuthorizationChallenge()
        val payload = authorizationDataSource.requestPasskey(challenge)
        val tokenInfo = authorizationDataSource.verifyAuthorization(
            createSignInData(challenge.requestId, payload.jsonPayload)
        )
        authorizationDataSource.saveAuthorizationTokens(tokenInfo)
        userProfileCRUD.updateFromRemote()
        authorizationDataSource.setAuthorizationStatus(true)
    }

    private suspend fun createSignInData(requestId: String, publicCredJson: String): SignInData {
        return SignInData(
            requestId = requestId,
            deviceInfo = SignInData.DeviceInfo(
                deviceUUID = deviceDataSource.getOrCreateDeviceUUID(),
                deviceName = getPlatformInformation().deviceName
            ),
            publicKeyCredentialJson = publicCredJson
        )
    }
}
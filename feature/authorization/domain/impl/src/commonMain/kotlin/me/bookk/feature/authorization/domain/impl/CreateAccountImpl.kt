package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.CreateAccount.UserData
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationData
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import me.bookk.feature.platform.domain.api.GetPlatformInformation

internal class CreateAccountImpl(
    private val registrationDataSource: RegistrationDataSource,
    private val deviceDataSource: DeviceDataSource,
    private val authorizationDataSource: AuthorizationDataSource,
    private val getPlatformInformation: GetPlatformInformation,
    private val userProfileCRUD: UserProfileCRUD,
) : CreateAccount {

    override suspend fun invoke(userData: UserData) {
        val challenge = registrationDataSource.getSignUpPasskeyChallenge(userData)
        val verificationPayload = registrationDataSource.createPasskey(challenge)
        val data = createRegistrationData(userData, verificationPayload, challenge.requestId)
        val tokenInfo = registrationDataSource.finishRegistration(data)
        authorizationDataSource.saveAuthorizationTokens(tokenInfo)
        userProfileCRUD.updateFromRemote()
        authorizationDataSource.setAuthorizationStatus(true)
    }

    private suspend fun createRegistrationData(
        userData: UserData,
        payload: PasskeyVerificationPayload,
        requestId: String
    ): RegistrationData {
        return RegistrationData(
            requestId = requestId,
            deviceInfo = RegistrationData.DeviceInfo(
                deviceUUID = deviceDataSource.getOrCreateDeviceUUID(),
                deviceName = getPlatformInformation().deviceName
            ),
            userInfo = RegistrationData.UserInfo(
                name = userData.firstName,
                lastName = userData.lastName,
                email = userData.email
            ),
            publicKeyCredentialJson = payload.jsonPayload
        )
    }
}
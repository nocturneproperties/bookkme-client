package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.CreateAccount.UserData
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
    private val getPlatformInformation: GetPlatformInformation
) : CreateAccount {

    override suspend fun invoke(userData: UserData) {
        val challenge = registrationDataSource.getSignUpPasskeyChallenge(userData)
        val verificationPayload = registrationDataSource.createPasskey(challenge)
        val data = createRegistrationData(userData, verificationPayload, challenge.userId)
        val tokenInfo = registrationDataSource.finishRegistration(data)
        authorizationDataSource.saveAuthorizationTokens(tokenInfo)
    }

    private suspend fun createRegistrationData(
        userData: UserData,
        payload: PasskeyVerificationPayload,
        userId: String
    ): RegistrationData {
        return RegistrationData(
            deviceInfo = RegistrationData.DeviceInfo(
                deviceUUID = deviceDataSource.getOrCreateDeviceUUID(),
                deviceName = getPlatformInformation().deviceName
            ),
            userInfo = RegistrationData.UserInfo(
                id = userId,
                name = userData.firstName,
                lastName = userData.lastName,
                email = userData.email
            ),
            publicKeyCredentialJson = payload.jsonPayload
        )
    }
}
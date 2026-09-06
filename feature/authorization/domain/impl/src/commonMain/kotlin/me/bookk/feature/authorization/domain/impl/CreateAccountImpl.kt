package me.bookk.feature.authorization.domain.impl

import library.device.api.DeviceFacade
import me.bookk.core.Logger
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.entity.businessOrThrow
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.CreateAccount.UserData
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationData
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import me.bookk.feature.authorization.domain.datasource.registration.ServerSignUpChallenge
import me.bookk.feature.authorization.domain.entity.TokenInfo

internal class CreateAccountImpl(
    private val registrationDataSource: RegistrationDataSource,
    private val deviceDataSource: DeviceDataSource,
    private val authorizationDataSource: AuthorizationDataSource,
    private val deviceFacade: DeviceFacade,
    private val passKeyManager: PassKeyManager,
    private val initialAppDataFetch: InitialAppDataFetch
) : CreateAccount {

    private val logger = Logger.create("CreateAccountImpl")

    override suspend fun invoke(userData: UserData) {
        val challenge = obtainRegistrationChallenge(userData)
        val verificationPayload = createPasskeyFrom(challenge)
        val data = createRegistrationData(userData, verificationPayload, challenge.requestId)
        val tokenInfo = finishRegistration(data)
        authorizationDataSource.saveAuthorizationTokens(tokenInfo)
        authorizationDataSource.invalidateClientTokens()
        initialAppDataFetch.rawFetch()
        authorizationDataSource.setAuthorizationStatus(true)
    }

    private suspend fun finishRegistration(data: RegistrationData): TokenInfo {
        return runCatching { registrationDataSource.finishRegistration(data) }
            .getOrElse {
                throw when (it.businessOrThrow().errorCode) {
                    AuthErrorCodes.USER_ALREADY_EXIST -> CreateAccount.Error.EmailAlreadyExist()
                    AuthErrorCodes.INVALID_EMAIL_FORMAT -> CreateAccount.Error.InvalidEmailFormat()
                    AuthErrorCodes.VERIFICATION_FAILED -> CreateAccount.Error.PasskeyVerificationFailed()
                    AuthErrorCodes.ACCOUNT_CREATION_FAILED -> CreateAccount.Error.AccountCreationFailed()
                    else -> it
                }
            }
    }

    private suspend fun obtainRegistrationChallenge(userData: UserData): ServerSignUpChallenge {
        return runCatching { registrationDataSource.getSignUpPasskeyChallenge(userData) }
            .getOrElse {
                throw when (it.businessOrThrow().errorCode) {
                    AuthErrorCodes.EMAIL_EXIST -> CreateAccount.Error.EmailAlreadyExist()
                    AuthErrorCodes.INVALID_EMAIL_FORMAT -> CreateAccount.Error.InvalidEmailFormat()
                    else -> it
                }
            }
    }

    private suspend fun createPasskeyFrom(challenge: ServerSignUpChallenge): PasskeyVerificationPayload {
        return runCatching {
            passKeyManager.create(
                PassKeyManager.CreationRequest(
                    userId = challenge.userHandle,
                    userName = challenge.displayName,
                    challengeJson = challenge.jsonChallengeData,
                    challenge = challenge.challenge
                )
            )
        }.getOrElse {
            logger.e(it)
            throw when (it) {
                is PassKeyManager.Error.UserCancelled -> Error.Ignore(it)
                else -> CreateAccount.Error.AccountCreationFailed()
            }
        }
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
                deviceName = deviceFacade.getDeviceName()
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
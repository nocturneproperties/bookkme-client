package me.bookk.feature.authorization.domain.impl

import library.device.api.DeviceFacade
import me.bookk.core.Logger
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.entity.businessOrThrow
import me.bookk.feature.authorization.domain.api.FinishSignUp
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationData
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import me.bookk.feature.authorization.domain.datasource.registration.ServerSignUpChallenge
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSessionDataSource
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.domain.entity.TokenInfo

internal class FinishSignUpImpl(
    private val registrationDataSource: RegistrationDataSource,
    private val deviceDataSource: DeviceDataSource,
    private val authorizationDataSource: AuthorizationDataSource,
    private val deviceFacade: DeviceFacade,
    private val passKeyManager: PassKeyManager,
    private val initialAppDataFetch: InitialAppDataFetch,
    private val signUpSessionDataSource: SignUpSessionDataSource,
) : FinishSignUp {

    private val logger = Logger.create("FinishSignUpImpl")

    override suspend fun invoke(code: String) {
        val session = signUpSessionDataSource.get()
        val challenge = session?.challenge ?: throw FinishSignUp.Error.SignUpNotStarted()
        val attestation = session.attestation ?: createAttestation(session, challenge)
        val data = createRegistrationData(session, challenge, attestation, code)
        val tokenInfo = finishRegistration(data)
        signUpSessionDataSource.clear()
        authorizationDataSource.saveAuthorizationTokens(tokenInfo)
        authorizationDataSource.invalidateClientTokens()
        initialAppDataFetch.rawFetch()
        authorizationDataSource.setAuthorizationStatus(true)
    }

    private suspend fun createAttestation(
        session: SignUpSession,
        challenge: ServerSignUpChallenge,
    ): PasskeyVerificationPayload {
        val attestation = runCatching {
            passKeyManager.create(
                PassKeyManager.CreationRequest(
                    userId = challenge.userHandle,
                    userName = challenge.displayName,
                    challengeJson = challenge.jsonChallengeData,
                    challenge = challenge.challenge,
                ),
            )
        }
            .getOrElse {
                logger.e(it)
                throw when (it) {
                    is PassKeyManager.Error.UserCancelled -> Error.Ignore(it)
                    else -> FinishSignUp.Error.AccountCreationFailed()
                }
            }
        signUpSessionDataSource.save(session.copy(attestation = attestation))
        return attestation
    }

    private suspend fun finishRegistration(data: RegistrationData): TokenInfo {
        return runCatching { registrationDataSource.finishRegistration(data) }
            .getOrElse {
                val error = it.businessOrThrow().toFinishError() ?: throw it
                if (error !is EmailVerificationError.CodeInvalid) {
                    dropChallenge(error)
                }
                throw error
            }
    }

    private fun dropChallenge(error: Throwable) {
        val session = signUpSessionDataSource.get() ?: return
        signUpSessionDataSource.save(session.dropped(error.toSignUpRejection()))
    }

    private fun Error.BusinessError.toFinishError(): Throwable? {
        return toEmailVerificationError() ?: when (errorCode) {
            AuthErrorCodes.USER_ALREADY_EXIST -> {
                FinishSignUp.Error.EmailAlreadyExist()
            }

            AuthErrorCodes.INVALID_EMAIL_FORMAT -> {
                FinishSignUp.Error.InvalidEmailFormat()
            }

            AuthErrorCodes.VERIFICATION_FAILED -> {
                FinishSignUp.Error.PasskeyVerificationFailed()
            }

            AuthErrorCodes.ACCOUNT_CREATION_FAILED -> {
                FinishSignUp.Error.AccountCreationFailed()
            }

            AuthErrorCodes.CHALLENGE_WINDOW_EXPIRED -> {
                FinishSignUp.Error.ChallengeWindowExpired()
            }

            AuthErrorCodes.LEGAL_DOCUMENT_VERSION_UNKNOWN -> {
                FinishSignUp.Error.LegalDocumentVersionUnknown()
            }

            AuthErrorCodes.LEGAL_DOCUMENTS_NOT_ACCEPTED -> {
                FinishSignUp.Error.LegalDocumentsNotAccepted()
            }

            else -> {
                null
            }
        }
    }

    private suspend fun createRegistrationData(
        session: SignUpSession,
        challenge: ServerSignUpChallenge,
        attestation: PasskeyVerificationPayload,
        code: String,
    ): RegistrationData {
        val userData = session.userData
        return RegistrationData(
            requestId = challenge.requestId,
            deviceInfo = RegistrationData.DeviceInfo(
                deviceUUID = deviceDataSource.getOrCreateDeviceUUID(),
                deviceName = deviceFacade.getDeviceName(),
            ),
            userInfo = RegistrationData.UserInfo(
                name = userData.firstName,
                lastName = userData.lastName,
                email = userData.email,
            ),
            publicKeyCredentialJson = attestation.jsonPayload,
            acceptedDocuments = userData.acceptedDocuments,
            verificationId = challenge.emailVerification.id,
            emailCode = code,
        )
    }
}

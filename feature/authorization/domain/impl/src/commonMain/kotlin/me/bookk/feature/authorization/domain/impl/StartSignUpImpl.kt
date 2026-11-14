package me.bookk.feature.authorization.domain.impl

import me.bookk.core.domain.entity.businessOrThrow
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.api.StartSignUp.UserData
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import me.bookk.feature.authorization.domain.datasource.registration.ServerSignUpChallenge
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSessionDataSource

internal class StartSignUpImpl(
    private val registrationDataSource: RegistrationDataSource,
    private val signUpSessionDataSource: SignUpSessionDataSource,
) : StartSignUp {

    override suspend fun invoke(userData: UserData): StartSignUp.PendingSignUp {
        val trimmedUserData = userData.trimmed()
        val challenge = obtainChallenge(trimmedUserData)
        signUpSessionDataSource.save(SignUpSession(trimmedUserData, challenge))
        return StartSignUp.PendingSignUp(
            maskedEmail = trimmedUserData.email.masked(),
            expiresAt = challenge.emailVerification.expiresAt,
            resendAvailableAt = challenge.emailVerification.resendAvailableAt,
        )
    }

    private suspend fun obtainChallenge(userData: UserData): ServerSignUpChallenge {
        return runCatching { registrationDataSource.getSignUpPasskeyChallenge(userData) }
            .onBusinessError { error ->
                throw error.toEmailVerificationError() ?: when (error.errorCode) {
                    AuthErrorCodes.EMAIL_EXIST -> StartSignUp.Error.EmailAlreadyExist()
                    AuthErrorCodes.INVALID_EMAIL_FORMAT -> StartSignUp.Error.InvalidEmailFormat()
                    else -> error
                }
            }.getOrThrow()
    }

    private fun UserData.trimmed(): UserData {
        return UserData(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = email.trim(),
            acceptedDocuments = acceptedDocuments,
        )
    }

    private fun String.masked(): String {
        return "${take(1)}***${substring(indexOf('@').coerceAtLeast(0))}"
    }
}

package me.bookk.feature.authorization.domain.datasource.registration

import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.entity.TokenInfo

interface RegistrationDataSource {
    suspend fun getSignUpPasskeyChallenge(userData: CreateAccount.UserData): ServerChallenge
    suspend fun finishRegistration(registrationData: RegistrationData): TokenInfo
    suspend fun createPasskey(challenge: ServerChallenge): PasskeyVerificationPayload
}
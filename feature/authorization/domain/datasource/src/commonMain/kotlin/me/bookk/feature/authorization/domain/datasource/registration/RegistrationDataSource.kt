package me.bookk.feature.authorization.domain.datasource.registration

import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.entity.TokenInfo

interface RegistrationDataSource {
    suspend fun getSignUpPasskeyChallenge(userData: CreateAccount.UserData): ServerSignUpChallenge
    suspend fun finishRegistration(data: RegistrationData): TokenInfo
    suspend fun createPasskey(challenge: ServerSignUpChallenge): PasskeyVerificationPayload
}
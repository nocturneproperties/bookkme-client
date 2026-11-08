package me.bookk.feature.authorization.domain.datasource.registration

import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.entity.TokenInfo

interface RegistrationDataSource {
    suspend fun getSignUpPasskeyChallenge(userData: StartSignUp.UserData): ServerSignUpChallenge
    suspend fun finishRegistration(data: RegistrationData): TokenInfo
}

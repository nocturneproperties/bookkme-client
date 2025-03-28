package me.bookk.feature.settings.domain.datasource.passkey

import me.bookk.feature.settings.domain.api.entity.Passkey

interface PasskeySettingsDataSource {
    suspend fun getPasskeys(): List<Passkey>
    suspend fun deletePasskey(id: Long)
    suspend fun getRegistrationChallengeForNewPasskey(): ServerSignUpChallenge
    suspend fun sendVerifiedPasskey(data: ClientSignUpResult)
}
package me.bookk.feature.settings.domain.datasource.passkey

import kotlin.uuid.Uuid

interface PasskeySettingsDataSource {
    suspend fun getPasskeys(): AccountPasskeys
    suspend fun deletePasskey(id: Uuid)
    suspend fun getRegistrationChallengeForNewPasskey(): AddPasskeyChallenge
    suspend fun sendVerifiedPasskey(data: ClientSignUpResult)
}

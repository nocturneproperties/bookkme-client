package me.bookk.feature.settings.domain.datasource.passkey

import me.bookk.feature.settings.domain.api.entity.Passkey
import kotlin.uuid.Uuid

interface PasskeySettingsDataSource {
    suspend fun getPasskeys(): List<Passkey>
    suspend fun deletePasskey(id: Uuid)
    suspend fun getRegistrationChallengeForNewPasskey(): ServerSignUpChallenge
    suspend fun sendVerifiedPasskey(data: ClientSignUpResult)
}
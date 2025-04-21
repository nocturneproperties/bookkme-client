package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.Error
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.settings.domain.api.CreateNewPasskey
import me.bookk.feature.settings.domain.api.GetAvailablePasskeys
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.domain.datasource.passkey.ClientSignUpResult
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import me.bookk.feature.settings.domain.datasource.passkey.ServerSignUpChallenge

internal class CreateNewPasskeyImpl(
    private val passkeySettingsDataSource: PasskeySettingsDataSource,
    private val passKeyManager: PassKeyManager,
    private val getAvailablePasskeys: GetAvailablePasskeys
) : CreateNewPasskey {
    override suspend fun invoke(): List<Passkey> {
        val challenge = passkeySettingsDataSource.getRegistrationChallengeForNewPasskey()
        val payload = createPasskeyFrom(challenge)
        passkeySettingsDataSource.sendVerifiedPasskey(
            ClientSignUpResult(
                requestId = challenge.requestId,
                publicKeyCredentialJson = payload.jsonPayload
            )
        )
        return getAvailablePasskeys()
    }

    private suspend fun createPasskeyFrom(challenge: ServerSignUpChallenge): PasskeyVerificationPayload {
        return runCatching {
            passKeyManager.create(
                PassKeyManager.ChallengeRequest(
                    userId = challenge.requestId,
                    userName = challenge.displayName,
                    challengeJson = challenge.jsonChallengeData
                )
            )
        }.getOrElse {
            throw when (it) {
                PassKeyManager.Error.UserCancelled -> Error.Ignore(it)
                else -> CreateNewPasskey.Error.AccountCreationFailed
            }
        }
    }

}
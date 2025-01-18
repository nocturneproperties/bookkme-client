package me.bookk.feature.authorization.data.local

import android.content.Context
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.domerrors.NotAllowedError
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationError
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.ServerChallenge

actual class PassKeyCreator(
    private val context: Context
) {

    private val credentialManager = CredentialManager.create(context.applicationContext)

    actual suspend fun create(challenge: ServerChallenge): PasskeyVerificationPayload {
        return runCatching {
            val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
                requestJson = challenge.jsonChallengeData
            )
            val response = credentialManager.createCredential(
                context = context,
                request = createPublicKeyCredentialRequest,
            )
            when (response) {
                is CreatePublicKeyCredentialResponse -> {
                    PasskeyVerificationPayload(response.registrationResponseJson)
                }

                else -> throw PasskeyVerificationError.Unknown
            }
        }.recoverCatching {
            throw when (it) {
                is CreateCredentialCancellationException -> PasskeyVerificationError.UserCancelled
                is CreatePublicKeyCredentialDomException -> when(it.domError) {
                    is NotAllowedError -> PasskeyVerificationError.UserCancelled
                    else -> PasskeyVerificationError.Infrastructure
                }
                is CreateCredentialInterruptedException,
                is CreateCredentialProviderConfigurationException -> PasskeyVerificationError.Infrastructure

                else -> PasskeyVerificationError.Unknown
            }
        }.getOrThrow()
    }

    private suspend fun authorize(challenge: ServerChallenge): String {
        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = challenge.jsonChallengeData
        )
        val request = GetCredentialRequest(listOf(getPublicKeyCredentialOption))
        val result = credentialManager.getCredential(
            context = context,
            request = request
        )
        when (val credential = result.credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                return responseJson
            }

            else -> throw PasskeyVerificationError.Unknown
        }
    }
}
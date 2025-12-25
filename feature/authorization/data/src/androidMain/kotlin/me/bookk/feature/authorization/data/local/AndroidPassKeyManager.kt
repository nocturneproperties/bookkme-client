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
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.domerrors.NotAllowedError
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager.ChallengeRequest
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import org.json.JSONObject

class AndroidPassKeyManager(
    private val context: Context
) : PassKeyManager {

    private val credentialManager = CredentialManager.create(context.applicationContext)

    override suspend fun create(challenge: ChallengeRequest): PasskeyVerificationPayload {
        return runCatching {
            val omittedPublicKeyObject =
                JSONObject(challenge.challengeJson).getJSONObject("publicKey").toString()
            val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
                requestJson = omittedPublicKeyObject
            )
            val response = credentialManager.createCredential(
                context = context,
                request = createPublicKeyCredentialRequest,
            )
            when (response) {
                is CreatePublicKeyCredentialResponse -> {
                    PasskeyVerificationPayload(response.registrationResponseJson)
                }

                else -> throw PassKeyManager.Error.Unknown(null)
            }
        }.recoverCatching {
            throw when (it) {
                is CreateCredentialCancellationException -> PassKeyManager.Error.UserCancelled
                is CreatePublicKeyCredentialDomException -> when (it.domError) {
                    is NotAllowedError -> PassKeyManager.Error.UserCancelled
                    else -> PassKeyManager.Error.Infrastructure
                }

                is CreateCredentialInterruptedException,
                is CreateCredentialProviderConfigurationException -> PassKeyManager.Error.Infrastructure

                else -> PassKeyManager.Error.Unknown(it)
            }
        }.getOrThrow()
    }

    override suspend fun authorize(jsonChallenge: String): PasskeyVerificationPayload {
        return runCatching {
            val omittedPublicKeyObject =
                JSONObject(jsonChallenge).getJSONObject("publicKey").toString()
            val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
                requestJson = omittedPublicKeyObject
            )
            val request = GetCredentialRequest(listOf(getPublicKeyCredentialOption))
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            when (val credential = result.credential) {
                is PublicKeyCredential -> {
                    val responseJson = credential.authenticationResponseJson
                    PasskeyVerificationPayload(responseJson)
                }

                else -> throw PassKeyManager.Error.Unknown(null)
            }
        }.recoverCatching {
            throw when (it) {
                is NoCredentialException -> PassKeyManager.Error.CredentialsMissing
                is GetCredentialCancellationException -> PassKeyManager.Error.UserCancelled
                else -> PassKeyManager.Error.Unknown(it)
            }
        }.getOrThrow()
    }
}
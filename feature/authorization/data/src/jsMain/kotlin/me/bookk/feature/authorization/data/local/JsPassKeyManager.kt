package me.bookk.feature.authorization.data.local

import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload

internal class JsPassKeyManager : PassKeyManager {

    override suspend fun create(
        challenge: PassKeyManager.CreationRequest,
    ): PasskeyVerificationPayload {
        throw unsupported()
    }

    override suspend fun authorize(
        challenge: PassKeyManager.AuthorizationRequest,
    ): PasskeyVerificationPayload {
        throw PassKeyManager.Error.CredentialsMissing()
    }

    override suspend fun signalUnknownPasskey(assertion: PasskeyVerificationPayload) {
        throw unsupported()
    }

    override suspend fun signalAcceptedPasskeys(passkeys: PassKeyManager.AcceptedPasskeys) {
        throw unsupported()
    }

    private fun unsupported(): UnsupportedOperationException {
        return UnsupportedOperationException("Passkeys are not supported on the web yet")
    }
}

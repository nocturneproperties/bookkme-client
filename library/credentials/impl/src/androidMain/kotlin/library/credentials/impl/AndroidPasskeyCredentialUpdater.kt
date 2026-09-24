package library.credentials.impl

import androidx.credentials.CredentialManager
import androidx.credentials.SignalAllAcceptedCredentialIdsRequest
import androidx.credentials.SignalUnknownCredentialRequest
import library.credentials.api.PasskeyCredentialUpdater
import me.bookk.core.android.AndroidActivityAware
import org.json.JSONArray
import org.json.JSONObject

class AndroidPasskeyCredentialUpdater : PasskeyCredentialUpdater, AndroidActivityAware() {

    override suspend fun reportUnknownCredential(relyingParty: String, credentialId: String) {
        val requestJson = JSONObject()
            .put("rpId", relyingParty)
            .put("credentialId", credentialId)
        credentialManager().signalCredentialState(SignalUnknownCredentialRequest(requestJson.toString()))
    }

    override suspend fun reportNoAcceptedCredentials(relyingParty: String, userHandle: String) {
        val requestJson = JSONObject()
            .put("rpId", relyingParty)
            .put("userId", userHandle)
            .put("allAcceptedCredentialIds", JSONArray())
        credentialManager().signalCredentialState(SignalAllAcceptedCredentialIdsRequest(requestJson.toString()))
    }

    private suspend fun credentialManager(): CredentialManager {
        return CredentialManager.create(awaitActivity())
    }
}

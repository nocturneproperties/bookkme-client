package library.credentials.api

interface PasskeyCredentialUpdater {
    suspend fun reportUnknownCredential(relyingParty: String, credentialId: String)
    suspend fun reportNoAcceptedCredentials(relyingParty: String, userHandle: String)
}

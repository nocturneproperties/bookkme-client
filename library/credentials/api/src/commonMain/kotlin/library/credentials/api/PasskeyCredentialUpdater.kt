package library.credentials.api

interface PasskeyCredentialUpdater {
    suspend fun reportUnknownCredential(relyingParty: String, credentialId: String)
    suspend fun reportAcceptedCredentials(
        relyingParty: String,
        userHandle: String,
        credentialIds: List<String>,
    )
}

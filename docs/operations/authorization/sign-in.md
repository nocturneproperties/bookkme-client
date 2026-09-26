[← Operations](../README.md)

# Sign in (passkey)

`SignIn()` → `GET /api/auth/passkey/sign_in/challenge`, then `POST /api/auth/sign_in`
· called from `SignInViewModel`

It follows the same steps as [Create account](create-account.md) after the passkey step, and shares its
"`authorized` is written last" rule and its partial-failure behaviour.

```mermaid
flowchart TD
    Start([invoke]) --> Ch[AuthorizationDataSource.getAuthorizationChallenge<br/>GET /api/auth/passkey/sign_in/challenge]
    Ch --> PK[PassKeyManager.authorize challenge]
    PK -- UserCancelled --> Ign([throw Error.Ignore])
    PK -- CredentialsMissing --> E1([throw Error.NoCredentialsAvailable])
    PK -- other failure --> E2([throw Error.PasskeyVerificationFailed])
    PK -- payload --> Dev[DeviceDataSource.getOrCreateDeviceUUID<br/>+ DeviceFacade.getDeviceName]
    Dev --> Ver[AuthorizationDataSource.verifyAuthorization<br/>POST /api/auth/sign_in]
    Ver -- business error --> C{errorCode}
    C -- 7 PASSKEY_OWNER_NOT_FOUND --> E3([throw Error.NoAccountForThisPasskey])
    C -- 4 VERIFICATION_FAILED --> E2
    C -- 6 CHALLENGE_WINDOW_EXPIRED --> E2
    C -- other --> EX([rethrow])
    Ver -- tokens --> Save[authorization_prefs: access_token, refresh_token]
    Save --> Inv[invalidateClientTokens]
    Inv --> Fetch[InitialAppDataFetch.rawFetch]
    Fetch --> Auth[authorization_prefs: authorized := true]
    Auth --> R([Unit])
```

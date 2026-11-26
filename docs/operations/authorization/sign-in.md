[← Operations](../README.md)

# Sign in (passkey)

`SignIn()` → `GET /api/auth/passkey/sign_in/challenge`, then `POST /api/auth/sign_in`
· called from `SignInViewModel`

It follows the same steps as [Finish sign-up](finish-sign-up.md) after the passkey step, and shares its
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
    C -- 26 PASSKEY_UNKNOWN --> Forget[PassKeyManager.signalUnknownPasskey payload<br/>best effort: the device forgets the passkey]
    Forget --> E3
    C -- 4 VERIFICATION_FAILED --> E2
    C -- 6 CHALLENGE_WINDOW_EXPIRED --> E2
    C -- other --> EX([rethrow])
    Ver -- tokens --> Save[authorization_prefs: access_token, refresh_token]
    Save --> Inv[invalidateClientTokens]
    Inv --> Fetch[InitialAppDataFetch.rawFetch]
    Fetch --> Auth[authorization_prefs: authorized := true]
    Auth --> R([Unit])
```

`PASSKEY_UNKNOWN` (26) means no account has the passkey any more (deleted from the account or by the
backend). The device's passkey provider is told to forget it (`reportUnknownCredential` on Android's Credential
Manager and iOS 26's `ASCredentialUpdater`), so it stops being offered; a failure of that signal is ignored.

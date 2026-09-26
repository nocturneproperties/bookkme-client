[← Operations](../README.md)

# Create account (passkey sign-up)

`CreateAccount(userData)` → `POST /api/auth/passkey/sign_up/challenge`, then `POST /api/auth/sign_up`
· called from `SignUpViewModel`

A three-step WebAuthn registration: the server issues a challenge, the platform `PassKeyManager` creates the
credential, and the server verifies it and returns tokens. After the tokens are saved, the Ktor bearer cache
is invalidated so the next request loads the new tokens. Then `rawFetch()` runs. `authorized = true` is
written **last**. That flag is what `BootstrapViewModel` observes to navigate into the app, so the main screen
opens with the profile and business list already cached.

```mermaid
flowchart TD
    Start([invoke userData]) --> Ch[RegistrationDataSource.getSignUpPasskeyChallenge<br/>POST /api/auth/passkey/sign_up/challenge]
    Ch -- business error --> ChC{errorCode}
    ChC -- 1 EMAIL_EXIST --> E1([throw Error.EmailAlreadyExist])
    ChC -- 2 INVALID_EMAIL_FORMAT --> E2([throw Error.InvalidEmailFormat])
    ChC -- other --> EX([rethrow])
    Ch -- ok --> PK[PassKeyManager.create challenge]
    PK -- UserCancelled --> Ign([throw Error.Ignore])
    PK -- other failure --> E3([throw Error.AccountCreationFailed])
    PK -- payload --> Dev[DeviceDataSource.getOrCreateDeviceUUID<br/>+ DeviceFacade.getDeviceName]
    Dev --> Fin[RegistrationDataSource.finishRegistration<br/>POST /api/auth/sign_up]
    Fin -- business error --> FinC{errorCode}
    FinC -- 3 USER_ALREADY_EXIST --> E1
    FinC -- 2 INVALID_EMAIL_FORMAT --> E2
    FinC -- 4 VERIFICATION_FAILED --> E4([throw Error.PasskeyVerificationFailed])
    FinC -- 5 ACCOUNT_CREATION_FAILED --> E3
    FinC -- other --> EX
    Fin -- tokens --> Save[authorization_prefs: access_token, refresh_token]
    Save --> Inv[invalidateClientTokens<br/>BearerAuthProvider.clearToken]
    Inv --> Fetch[InitialAppDataFetch.rawFetch]
    Fetch --> Auth[authorization_prefs: authorized := true]
    Auth --> R([Unit])
```

Composes [Initial app data fetch](initial-app-data-fetch.md). A failure inside `rawFetch()` (for example
`GET /api/user/me`) propagates **after** the tokens are saved but **before** `authorized = true` is written. The
account exists and the tokens are stored, yet the app stays on the login screen.

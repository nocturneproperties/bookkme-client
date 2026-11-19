[← Operations](../README.md)

# Create new passkey

`CreateNewPasskey()` → `GET /api/auth/passkey/add/challenge`, then `POST /api/auth/passkey/add/finish`
· called from `PasskeyViewModel`

Registers an additional passkey on the signed-in account and returns the refreshed list. Nothing is cached.

An account holds at most 10 passkeys. The backend checks this on both calls and answers `PASSKEYS_LIMIT_REACHED`
(13), which maps to `Error.PasskeysLimitReached`; `PasskeyViewModel` shows it as a message. Finishing answers `204`.

```mermaid
flowchart TD
    Start([invoke]) --> Ch[PasskeySettingsDataSource.getRegistrationChallengeForNewPasskey<br/>GET /api/auth/passkey/add/challenge]
    Ch --> PK[PassKeyManager.create challenge]
    Ch -- 10 UNABLE_TO_GENERATE_REGISTRATION_CHALLENGE --> E2([throw Error.ChallengeCreationFailed])
    Ch -- 13 PASSKEYS_LIMIT_REACHED --> E3([throw Error.PasskeysLimitReached])
    PK -- UserCancelled --> Ign([throw Error.Ignore])
    PK -- other failure --> E1([throw Error.AccountCreationFailed])
    PK -- payload --> Fin[sendVerifiedPasskey requestId, credentialJson<br/>POST /api/auth/passkey/add/finish]
    Fin -- 204 --> List[GetAvailablePasskeys<br/>GET /api/auth/passkey]
    Fin -- 13 PASSKEYS_LIMIT_REACHED --> E3
    Fin -- other error --> EX([rethrow])
    List --> R([return List Passkey])
```

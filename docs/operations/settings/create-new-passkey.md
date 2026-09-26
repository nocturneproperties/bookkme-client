[← Operations](../README.md)

# Create new passkey

`CreateNewPasskey()` → `GET /api/auth/passkey/add/challenge`, then `POST /api/auth/passkey/add/finish`
· called from `PasskeyViewModel`

Registers an additional passkey on the signed-in account and returns the refreshed list. Nothing is cached.

```mermaid
flowchart TD
    Start([invoke]) --> Ch[PasskeySettingsDataSource.getRegistrationChallengeForNewPasskey<br/>GET /api/auth/passkey/add/challenge]
    Ch --> PK[PassKeyManager.create challenge]
    PK -- UserCancelled --> Ign([throw Error.Ignore])
    PK -- other failure --> E1([throw Error.AccountCreationFailed])
    PK -- payload --> Fin[sendVerifiedPasskey requestId, credentialJson<br/>POST /api/auth/passkey/add/finish]
    Fin --> List[GetAvailablePasskeys<br/>GET /api/auth/passkey]
    List --> R([return List Passkey])
```

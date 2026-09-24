[← Operations](../README.md)

# Delete account

`DeleteAccount()` → `GET /api/auth/passkey/sign_in/challenge`, then `DELETE /api/auth/account`
· called from `DeleteAccountViewModel`

The user re-authenticates with a passkey, and the signed assertion authorizes the deletion. On success the
tokens are cleared and `authorized` is set to false, which sends the app to Login.

```mermaid
flowchart TD
    Start([invoke]) --> Ch[AuthorizationDataSource.getAuthorizationChallenge<br/>GET /api/auth/passkey/sign_in/challenge]
    Ch --> PK[PassKeyManager.authorize]
    PK -- UserCancelled --> Ign([throw Error.Ignore])
    PK -- any other failure --> E1([throw Error.AccountVerificationFailed])
    PK -- payload --> Del[AuthorizationDataSource.deleteAccount<br/>DELETE /api/auth/account]
    Del -- business error --> Code{errorCode}
    Code -- 4 VERIFICATION_FAILED --> E1
    Code -- other --> EX([rethrow])
    Del -- network / other --> EX
    Del -- 2xx --> Out[LogOut<br/>clears every cache table and prefs bucket]
    Out --> Clr[authorization_prefs: tokens := null]
    Clr --> Auth[authorization_prefs: authorized := false]
    Auth --> R([Unit])
```

A failure before or during the remote deletion leaves the session and caches untouched. After a successful
deletion, [Log out](../authorization/log-out.md) runs every `LogOutAction`. Its `DELETE /api/auth/session`
call fails because the account no longer exists, and that failure is swallowed like any other logout
action error.

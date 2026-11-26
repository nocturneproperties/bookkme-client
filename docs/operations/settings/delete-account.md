[← Operations](../README.md)

# Delete account

`DeleteAccount()` returns an `ActionOutcome<Unit>`. The mobile apps bind `DeleteAccountImpl`, which composes
`UserProfileCRUD.get()`, `ConfirmSensitiveAction` and `LogOut` and returns `ActionOutcome.Completed(Unit)`.
The client web app binds `TelegramDeleteAccountImpl` instead (see [Telegram Mini App](#telegram-mini-app)).
Called from `DeleteAccountViewModel`.

The caller confirms `DELETE_ACCOUNT` with the nil business UUID and their own user UUID as target.
The shared confirmation operation obtains the challenge, prompts for a passkey and exchanges its
assertion at `POST /api/auth/passkey/confirm_action`. Account deletion sends only the resulting
`confirmationToken` to `DELETE /api/auth/account`; the assertion itself is not sent to that endpoint.

```mermaid
flowchart TD
    Start([invoke]) --> Profile[UserProfileCRUD.get caller id]
    Profile --> Confirm[ConfirmSensitiveAction DELETE_ACCOUNT<br/>nil business UUID, caller id]
    Profile -- failure --> EX([propagate])
    Confirm -- shared Error.Ignore --> Ign([propagate silent cancellation])
    Confirm -- typed confirmation failure --> E1([throw Error.AccountVerificationFailed])
    Confirm -- other failure --> EX
    Confirm -- token and verified assertion --> Del[AuthorizationDataSource.deleteAccount<br/>DELETE /api/auth/account<br/>body confirmationToken]
    Del -- 12 ACTION_CONFIRMATION_INVALID --> E1
    Del -- 4 VERIFICATION_FAILED --> E1
    Del -- other failure --> EX
    Del -- 204 --> Sig[PassKeyManager.signalAccountDeleted<br/>reuse the verified assertion]
    Sig -- success or any failure --> Out[LogOut<br/>clears every cache table and prefs bucket]
    Out --> Clr[authorization_prefs: tokens := null]
    Clr --> Auth[authorization_prefs: authorized := false]
    Auth --> R([ActionOutcome.Completed Unit])
```

The confirmation token is single-use and expires after two minutes. Failed or cancelled confirmation
makes no DELETE request. A failure before or during remote deletion preserves the authenticated session
and does not run logout/cache cleanup or signal credential removal. The caller profile read can fill its
normal cache on a miss. No automatic retry of confirmation or the protected DELETE is made.

`signalAccountDeleted` is best-effort: any failure is swallowed because the account is already deleted on the
server. It reads `id` and `response.userHandle` from the assertion JSON and forwards them to
`PasskeyCredentialUpdater`, which each platform supplies through `StateFactoryCreator.createPasskeyCredentialUpdater()`.
Android (`AndroidPasskeyCredentialUpdater`) sends `SignalUnknownCredentialRequest` and
`SignalAllAcceptedCredentialIdsRequest` (empty list) via `CredentialManager.signalCredentialState`. iOS
(`IOSPasskeyCredentialUpdater`, in Swift because `ASCredentialUpdater` is Swift-only) calls
`reportUnknownPublicKeyCredential` and `reportAllAcceptedPublicKeyCredentials` (empty list). The all-accepted
signal is skipped when the assertion has no `userHandle`.

After a successful deletion, [Log out](../authorization/log-out.md) runs every `LogOutAction`. Its `DELETE /api/auth/session`
call fails because the account no longer exists, and that failure is swallowed like any other logout
action error.

## Telegram Mini App

`TelegramDeleteAccountImpl` only asks for a confirmation in the person's Telegram chat; the account is deleted
later by [Complete Telegram confirmation](complete-telegram-confirmation.md) when they tap Confirm there.

```mermaid
flowchart TD
    Start([invoke]) --> Profile[UserProfileCRUD.get caller id]
    Profile --> Send[StartTelegramActionConfirmation DELETE_ACCOUNT<br/>nil business UUID, caller id]
    Send -- Unit --> R([ActionOutcome.ConfirmationSent])
    Send -- Error.BotCannotWrite --> W([throw ChatConfirmationError.WriteAccessRequired])
    Send -- Error.TooFrequent --> F([throw ChatConfirmationError.TooFrequent])
    Send -- other failure --> EX([propagate])
```

`DeleteAccountViewModel` hands both outcomes to `ChatConfirmationRequest`, shared with Export my data:
`ConfirmationSent` (and `TooFrequent`, a message sent moments ago) shows "We sent a confirmation to your
Telegram chat. Tap Confirm there." and turns the button into "Send again", inactive for a 60-second cooldown;
`WriteAccessRequired` asks Telegram's `requestWriteAccess` (Bot API 6.9) and sends again once if allowed,
otherwise a popup explains that the confirmation needs a message from the bot.


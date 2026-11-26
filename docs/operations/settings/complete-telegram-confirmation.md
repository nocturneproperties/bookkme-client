[← Operations](../README.md)

# Complete Telegram confirmation

`CompleteTelegramConfirmation(action, initData)` finishes an account deletion or a personal data export that
the person confirmed in their Telegram chat. It composes `ConfirmActionWithTelegram` and runs the same
protected request as the passkey path, through the shared `AccountRemoval` and `PersonalDataExportRequest`
helpers. Bound only in the client web app (`clientSettingsDomainModule`) and called from
`ConfirmActionViewModel`, which the Mini App opens for an `op_delete-account_…` or `op_export-personal_…`
launch parameter (parsed by `LaunchParameter.parse` in `:core:domain`).

```mermaid
flowchart TD
    Start([invoke action, initData]) --> Confirm[ConfirmActionWithTelegram initData]
    Confirm -- Error.ConfirmationExpired --> Expired([throw Error.ConfirmationExpired])
    Confirm -- Error.TelegramAccountMismatch --> Other([throw Error.TelegramAccountMismatch])
    Confirm -- Error.TelegramDataInvalid --> Invalid([throw Error.TelegramDataInvalid])
    Confirm -- other failure --> EX([propagate])
    Confirm -- token --> Kind{action}
    Kind -- DELETE_ACCOUNT --> Del[AuthorizationDataSource.deleteAccount<br/>DELETE /api/auth/account<br/>body confirmationToken]
    Del -- 12 ACTION_CONFIRMATION_INVALID or 4 VERIFICATION_FAILED --> Expired
    Del -- other failure --> EX
    Del -- 204 --> Out[LogOut<br/>clears every cache table and prefs bucket]
    Out --> Clr[authorization_prefs: tokens := null]
    Clr --> Auth[authorization_prefs: authorized := false]
    Auth --> R([Unit])
    Kind -- EXPORT_PERSONAL_DATA --> Exp[DataExportDataSource.start<br/>POST /api/user/export<br/>body confirmationToken]
    Exp -- 100005 USER_ACTION_CONFIRMATION_INVALID --> Expired
    Exp -- 900002 DATA_EXPORT_TOO_FREQUENT --> Limit([throw Error.ExportTooFrequent])
    Exp -- other failure --> EX
    Exp -- success --> Prune[DataExportDataSource.deleteFiles]
    Prune --> Save[DataExportDataSource.saveInDb<br/>personal_data_export id = 0]
    Save --> R
```

Unlike the passkey path, the deletion makes no `PassKeyManager.signalAccountDeleted` call: there is no passkey
assertion in the Mini App. A failed confirmation makes no protected request and keeps the session. Nothing is
retried automatically.

On success the screen plays a success haptic; a deletion shows the success message and the cleared session
returns the Mini App to Telegram sign-in, an export replaces the screens above Home with Export my data, which
renders the RUNNING status from `personal_data_export`. Each error replaces the Confirm button with "Open
Settings". The Telegram `BackButton` leaves without confirming.

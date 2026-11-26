[← Operations](../README.md)

# Complete Telegram confirmation

`CompleteTelegramConfirmation(action, initData)` finishes an account deletion or a personal data export that
the person confirmed in their Telegram chat. It composes `ConfirmActionWithTelegram` and runs the same
protected request as the passkey path, through the shared `AccountDeletionScheduler` and `PersonalDataExportRequest`
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
    Kind -- DELETE_ACCOUNT --> Del[AuthorizationDataSource.requestAccountDeletion<br/>DELETE /api/auth/account<br/>body confirmationToken]
    Del -- 12 ACTION_CONFIRMATION_INVALID or 4 VERIFICATION_FAILED --> Expired
    Del -- other failure --> EX
    Del -- 200 deletesAt --> Save[AccountDeletionDataSource.saveScheduledDeletion<br/>account_deletion_prefs: deletes_at]
    Save --> R([Unit])
    Kind -- EXPORT_PERSONAL_DATA --> Exp[DataExportDataSource.start<br/>POST /api/user/export<br/>body confirmationToken]
    Exp -- 100005 USER_ACTION_CONFIRMATION_INVALID --> Expired
    Exp -- 900002 DATA_EXPORT_TOO_FREQUENT --> Limit([throw Error.ExportTooFrequent])
    Exp -- other failure --> EX
    Exp -- success --> Prune[DataExportDataSource.deleteFiles]
    Prune --> Save[DataExportDataSource.saveInDb<br/>personal_data_export id = 0]
    Save --> R
```

The deletion is only scheduled: the backend erases the account 7 days later (backend #208) and the
account keeps working until then, so the session is never cleared. A failed confirmation makes no protected
request. Nothing is retried automatically.

On success the screen plays a success haptic; a deletion replaces the screens above Home with Delete account, which shows the pending deletion and its date
(see [Get account deletion](get-account-deletion.md)) while the person stays signed in, an export replaces the screens above Home with Export my data, which
renders the RUNNING status from `personal_data_export`. Each error replaces the Confirm button with "Open
Settings". The Telegram `BackButton` leaves without confirming.

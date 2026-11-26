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

There is no confirmation screen: tapping Confirm in the chat is the confirmation. Home pushes
`ClientScreen.ConfirmAction(action)`, which opens Delete account or Export my data directly, and `ConfirmTelegramAction`
on that screen runs `ConfirmActionViewModel` once, which calls this operation with the raw `initData` as soon as it
opens. Both screens render from the local cache, so the saved result shows by itself: Delete account switches to the
requested deletion and its date (see [Get account deletion](get-account-deletion.md)) while the person stays signed
in, and Export my data to its preparing state from `personal_data_export`. Success plays a haptic; an expired or used
confirmation, another Telegram account's confirmation, stale Telegram data (reopen the Mini App from the chat
button) or an export already made today shows its message in a Telegram popup, and other failures show the mapped
error.

[← Operations](../README.md)

# Start personal data export

`StartPersonalDataExport` returns an `ActionOutcome<DataExport>`. The mobile apps bind
`StartPersonalDataExportImpl`: `POST /api/user/export`, then upsert the returned status in Room, returning
`ActionOutcome.Completed`. The client web app binds `TelegramStartPersonalDataExportImpl` instead (see
[Telegram Mini App](#telegram-mini-app)). The token request and the status save are shared with
[Complete Telegram confirmation](complete-telegram-confirmation.md) through `PersonalDataExportRequest`.

```mermaid
flowchart TD
    Start([invoke]) --> Profile[UserProfileCRUD.get<br/>caller id]
    Profile --> Confirm[ConfirmSensitiveAction<br/>EXPORT_PERSONAL_DATA, nil business id, caller id]
    Confirm -- token --> Net[DataExportDataSource.start<br/>POST /api/user/export<br/>body confirmationToken]
    Confirm -- confirmation error --> Failed([throw Error.ConfirmationFailed])
    Confirm -- shared Error.Ignore --> Cancel([propagate cancellation])
    Net -- 100005 USER_ACTION_CONFIRMATION_INVALID --> Expired([throw Error.ConfirmationRequired])
    Net -- 900002 DATA_EXPORT_TOO_FREQUENT --> Limit([throw Error.TooFrequent])
    Net -- success --> Prune[DataExportDataSource.deleteFiles<br/>clear personal_data_exports cache directory]
    Prune --> Save[DataExportDataSource.saveInDb]
    Save --> Upsert[PersonalDataExportDao.upsert<br/>personal_data_export id = 0]
    Upsert --> R([return ActionOutcome.Completed DataExport])
    Save -- failure --> Other
    Prune -- failure --> Other
    Net -- other failure --> Other([propagate])
```

Passkey confirmation is bound to the exact export action and target. The backend builds JSON in the
background and enforces the daily limit; the operation does not poll, save tokens, or retry protected writes.
Use the status operation to inspect progress and cooldown. A confirmation token is single-use and expires
after two minutes. Shared cancellation errors propagate without starting an export.

Settings → Export my data calls this operation only from the request button. A shared presentation
launch key excludes overlapping status reads, confirmations and downloads. Successful requests save
status through a separate datasource call and play one success haptic. The database observer renders
the screen; the start return value is not rendered independently. Confirmation failures appear inline and require another
explicit tap; cancellation is silent. `TooFrequent` hides the request and offers status refresh to obtain
the current availability timestamp. No confirmation token is stored by the presentation layer.

Confirmation and network failures never replace the saved status. Cache write errors propagate.
`CommonDataExportDataSource` is registered as both the datasource and a `LogOutAction`; logout clears
`personal_data_export`. The protected POST remains network-only inside the datasource.

Before saving a successful network status, the use case clears all personal export files through
`DataExportDataSource.deleteFiles()`, even when the READY status and timestamps are unchanged.
Only one personal export can exist at a time. Cleanup is scoped to `personal_data_exports/`, so
other export types can use separate directories without being affected. Failed network requests
preserve the downloaded files. Logout also removes this directory from the app cache. File operations
do not change the Room schema or add preferences. The datasource delegates directory removal to
`FileProvider.deleteRecursively()`; subsequent downloads recreate the directory when saving JSON.

## Telegram Mini App

`TelegramStartPersonalDataExportImpl` only asks for a confirmation in the person's Telegram chat; the export
starts when they tap Confirm there, through [Complete Telegram confirmation](complete-telegram-confirmation.md).

```mermaid
flowchart TD
    Start([invoke]) --> Profile[UserProfileCRUD.get caller id]
    Profile --> Send[StartTelegramActionConfirmation EXPORT_PERSONAL_DATA<br/>nil business UUID, caller id]
    Send -- Unit --> R([ActionOutcome.ConfirmationSent])
    Send -- Error.BotCannotWrite --> W([throw ChatConfirmationError.WriteAccessRequired])
    Send -- Error.TooFrequent --> F([throw ChatConfirmationError.TooFrequent])
    Send -- other failure --> EX([propagate])
```

The export screen stays on the request stage and `ChatConfirmationRequest` shows the sent notice, the
60-second "Send again" cooldown and the write-access request, as described in
[Delete account](delete-account.md#telegram-mini-app). Nothing is saved until the export starts.


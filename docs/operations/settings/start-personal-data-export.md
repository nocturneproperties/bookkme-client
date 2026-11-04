[← Operations](../README.md)

# Start personal data export

`StartPersonalDataExport` → `POST /api/user/export`. No local persistence.

```mermaid
flowchart TD
    Start([invoke]) --> Profile[UserProfileCRUD.get<br/>caller id]
    Profile --> Confirm[ConfirmSensitiveAction<br/>EXPORT_PERSONAL_DATA, nil business id, caller id]
    Confirm -- token --> Net[DataExportDataSource.start<br/>POST /api/user/export<br/>body confirmationToken]
    Confirm -- confirmation error --> Failed([throw Error.ConfirmationFailed])
    Confirm -- shared Error.Ignore --> Cancel([propagate cancellation])
    Net -- 100005 USER_ACTION_CONFIRMATION_INVALID --> Expired([throw Error.ConfirmationRequired])
    Net -- 900002 DATA_EXPORT_TOO_FREQUENT --> Limit([throw Error.TooFrequent])
    Net -- success --> R([return DataExport])
    Net -- other failure --> Other([propagate])
```

Passkey confirmation is bound to the exact export action and target. The backend builds JSON in the
background and enforces the daily limit; the operation does not poll, save tokens, or retry protected writes.
Use the status operation to inspect progress and cooldown. A confirmation token is single-use and expires
after two minutes. Shared cancellation errors propagate without starting an export.

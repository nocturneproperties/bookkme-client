[← Operations](../README.md)

# Start business data export

`StartBusinessDataExport` → `POST /api/business/{id}/export`. No local persistence.

```mermaid
flowchart TD
    Start([invoke businessId]) --> Confirm[ConfirmSensitiveAction<br/>EXPORT_BUSINESS_DATA, businessId, businessId]
    Confirm -- token --> Net["DataExportDataSource.start<br/>POST /api/business/{id}/export<br/>body confirmationToken"]
    Confirm -- confirmation error --> Failed([throw Error.ConfirmationFailed])
    Confirm -- shared Error.Ignore --> Cancel([propagate cancellation])
    Net -- 200040 BUSINESS_ACTION_CONFIRMATION_INVALID --> Expired([throw Error.ConfirmationRequired])
    Net -- 900002 DATA_EXPORT_TOO_FREQUENT --> Limit([throw Error.TooFrequent])
    Net -- success --> R([return DataExport])
    Net -- other failure --> Other([propagate])
```

Passkey confirmation is bound to the exact export action and target. The backend builds JSON in the
background and enforces the daily limit; the operation does not poll, save tokens, or retry protected writes.
Use the status operation to inspect progress and cooldown. A confirmation token is single-use and expires
after two minutes. Shared cancellation errors propagate without starting an export.

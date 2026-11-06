[← Operations](../README.md)

# Get personal data export

`GetPersonalDataExport` → `GET /api/user/export`. No local persistence.

```mermaid
flowchart TD
    Start([invoke]) --> Net[DataExportDataSource.get<br/>GET /api/user/export]
    Net -- success --> Map[Map status and nullable timestamps]
    Map --> R([return DataExport])
    Net -- failure --> Other([propagate mapped error])
```

Statuses are `NONE`, `RUNNING`, `READY`, `FAILED`. `requestedAt`, `expiresAt` and `availableAgainAt` are
nullable backend timestamps; the app does not infer expiry or fabricate a cooldown.

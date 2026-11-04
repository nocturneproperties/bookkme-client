[← Operations](../README.md)

# Get legal document statuses

`GetLegalDocumentStatuses.invoke()` reads the current backend records without persisting them.
There is no local cache or background observer. Empty results are returned unchanged.
Errors propagate through the standard datasource exception mapping; this operation adds no domain errors.

```mermaid
flowchart TD
    Start([invoke]) --> Fetch["LegalDocumentsDataSource.getStatuses<br/>GET /api/user/me/legal"]
    Fetch --> Map[Map remote records to domain records]
    Map --> Result([Return list])
    Fetch -- failure --> Error([Propagate mapped error])
```

`acceptedAt = null` means the current document has not been accepted. This operation only reads acceptance status. Signup sends accepted document versions through
[Create account](../authorization/create-account.md).

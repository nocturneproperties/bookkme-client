[← Operations](../README.md)

# Accept legal documents

`AcceptLegalDocuments.invoke()` → `POST /api/user/me/legal`. No local cache is read or written.

```mermaid
flowchart TD
    Start([invoke]) --> Net["LegalDocumentsDataSource.accept<br/>POST /api/user/me/legal"]
    Net -- 204 --> R([Unit])
    Net -- 100003 LEGAL_DOCUMENT_VERSION_UNKNOWN --> Error([throw Error.UnknownVersion])
    Net -- other failure --> Other([propagate])
```

The caller supplies the exact accepted versions. Null means leave the existing acceptance unchanged;
the operation does not fetch new versions or infer consent. Repeating the same acceptance is idempotent
on the backend. Use `GetLegalDocumentStatuses` to read the fresh status when needed.

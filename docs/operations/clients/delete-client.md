[← Operations](../README.md)

# Delete client

`DeleteClient(client)` → `DELETE /api/business/{businessId}/clients/{id}`
· called from `EditClientViewModel`

```mermaid
flowchart TD
    Start([invoke client]) --> Net[ClientsDataSource.deleteClient businessId, id<br/>DELETE /api/business/businessId/clients/id]
    Net -- 2xx --> Del[(deleteClientInDb id)]
    Del --> Emit[clientEvents.emit ClientEvent.Deleted]
    Emit --> R([Unit])
    Net -- error --> EX([rethrow])
```

**Emits:** `ClientEvent.Deleted`. **Consumed by:** nobody.

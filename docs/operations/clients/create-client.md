[← Operations](../README.md)

# Create client

`CreateClient(client)` → `POST /api/business/{businessId}/clients`
· called from `CreateClientViewModel`

The server does all validation (contact info, name, phone format, duplicate phone). The client maps no
business error codes, so every failure is rethrown as the generic error.

```mermaid
flowchart TD
    Start([invoke client]) --> Net[ClientsDataSource.createClient<br/>POST /api/business/businessId/clients]
    Net -- 2xx created --> Save[(saveClientsInDb listOf created)]
    Save --> Emit[clientEvents.emit ClientEvent.Created]
    Emit --> R([return created Client])
    Net -- error --> EX([rethrow])
```

**Emits:** `ClientEvent.Created`. **Consumed by:** nobody. The list redraws because it observes the `client` table.

[← Operations](../README.md)

# Edit client

`EditClient(client)` → `PATCH /api/business/{businessId}/clients/{id}`
· called from `EditClientViewModel`

The route is sent as **PATCH**, even though bookk-server documents the route as `PUT`. Check the swagger before
changing either side.

```mermaid
flowchart TD
    Start([invoke client]) --> Net[ClientsDataSource.updateClient<br/>PATCH /api/business/businessId/clients/id]
    Net -- 2xx updated --> Save[(saveClientsInDb listOf updated)]
    Save --> Emit[clientEvents.emit ClientEvent.Updated]
    Emit --> R([return updated Client])
    Net -- error --> EX([rethrow])
```

**Emits:** `ClientEvent.Updated`. **Consumed by:** `ClientDetailsViewModel`, which re-renders the open
details screen with the updated client.

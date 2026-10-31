[← Operations](../README.md)

# Edit service

`EditService(service)` → `PUT /api/business/{businessId}/service/{id}`
· called from `EditServiceViewModel`

The service name is trimmed before it is sent.

```mermaid
flowchart TD
    Start([invoke service]) --> Trim[service.name = name.trim]
    Trim --> Net[ServiceDataSource.editService<br/>PUT /api/business/businessId/service/id]
    Net -- 2xx updated --> Save[(saveServiceInDB updated)]
    Save --> R([return updated Service])
    Net -- error --> EX([rethrow])
```

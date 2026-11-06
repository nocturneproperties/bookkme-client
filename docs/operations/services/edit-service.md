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
    Net -- business error --> Code{errorCode}
    Code -- 200007 --> E200007([throw Error.NameAlreadyExists])
    Code -- 200008 --> E200008([throw Error.InvalidName])
    Code -- 200012 --> E200012([throw Error.GroupNotFound])
    Code -- 200037 --> E200037([throw Error.InvalidPricePrecision])
    Code -- 200038 --> E200038([throw Error.InvalidBusinessCurrency])
    Code -- other --> EX([rethrow])
```

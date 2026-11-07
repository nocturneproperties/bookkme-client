[← Operations](../README.md)

# Create service

`CreateService(service)` → `POST /api/business/{businessId}/service`
· called from `AddServiceViewModel`

The service name is trimmed before it is sent.

```mermaid
flowchart TD
    Start([invoke service]) --> Trim[service.name = name.trim]
    Trim --> Net[ServiceDataSource.createService<br/>POST /api/business/businessId/service]
    Net -- 2xx created --> Save[(saveServiceInDB created, upsert)]
    Save --> R([return created Service])
    Net -- business error --> Code{errorCode}
    Code -- 200007 --> E200007([throw Error.NameAlreadyExists])
    Code -- 200008 --> E200008([throw Error.InvalidName])
    Code -- 200012 --> E200012([throw Error.GroupNotFound])
    Code -- 200037 --> E200037([throw Error.InvalidPricePrecision])
    Code -- 200038 --> E200038([throw Error.InvalidBusinessCurrency])
    Code -- other --> EX([rethrow])
```

The local upsert assumes the service's group is already in `service_group`. If it is not, the
`service.groupId` foreign key fails and the create **looks failed even though it succeeded on the server**.
`AddServiceViewModel` only offers groups from [Get service groups](get-service-groups.md), which were cached.

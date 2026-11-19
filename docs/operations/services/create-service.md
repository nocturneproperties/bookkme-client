[← Operations](../README.md)

# Create service

`CreateService(service)` → `POST /api/business/{businessId}/service`
· called from `AddServiceViewModel`

The service name is trimmed before it is sent. A business with its limit of services (300 unless the backend
raises it) answers `BUSINESS_SERVICES_LIMIT_REACHED` (200047).

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
    Code -- 200047 SERVICES_LIMIT_REACHED --> E200047([throw Error.ServicesLimitReached])
    Code -- other --> EX([rethrow])
```

The local upsert assumes the service's group is already in `service_group`. If it is not, the
`service.groupId` foreign key fails and the create **looks failed even though it succeeded on the server**.
`AddServiceViewModel` only offers groups from [Get service groups](get-service-groups.md), which were cached.
